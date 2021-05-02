package com.jsql.model.suspendable;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.suspendable.callable.CallablePageSource;
import com.jsql.util.LogLevel;

/**
 * Runnable class, search the correct number of fields in the SQL query.
 * Concurrent search with stop capability
 */
public class SuspendableGetIndexes extends AbstractSuspendable {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    public SuspendableGetIndexes(InjectionModel injectionModel) {
        super(injectionModel);
    }

    @Override
    public String run(Object... args) throws JSqlException {
        
        // Concurrent search
        ExecutorService taskExecutor = this.injectionModel.getMediatorUtils().getThreadUtil().getExecutor("CallableGetIndexes");
        
        CompletionService<CallablePageSource> taskCompletionService = new ExecutorCompletionService<>(taskExecutor);

        String initialQuery = StringUtils.EMPTY;
        int nbIndex;
        
        int countNormalIndex =
            this.injectionModel.getMediatorUtils().getPreferencesUtil().isLimitingNormalIndex()
            ? this.injectionModel.getMediatorUtils().getPreferencesUtil().countNormalIndex()
            : 50;

        // SQL fields are built like 1337[index]7330+1
        // 7330+1 allows to exclude false positive when page contains injection URL
        // Search if the source contains 1337[index]7331
        for (nbIndex = 1 ; nbIndex <= countNormalIndex ; nbIndex++) {
            
            taskCompletionService.submit(
                new CallablePageSource(
                    this.injectionModel.getMediatorVendor().getVendor().instance().sqlIndices(nbIndex),
                    this.injectionModel,
                    "normal#" + nbIndex
                )
            );
        }
        
        nbIndex = 1;

        try {
            // Start from 10 to 100 requests
            while (nbIndex <= countNormalIndex) {

                if (this.isSuspended()) {
                    throw new StoppedByUserSlidingException();
                }

                CallablePageSource currentCallable = taskCompletionService.take().get();
                nbIndex++;

                // Found a correct mark 1337[index]7331 in the source
                if (Pattern.compile("(?s).*1337\\d+7331.*").matcher(currentCallable.getContent()).matches()) {
                    
                    this.injectionModel.getMediatorStrategy().getNormal().setSourceIndexesFound(currentCallable.getContent());
                    initialQuery = currentCallable.getQuery().replace("0%2b1", "1");
                    
                    if (this.injectionModel.getMediatorUtils().getPreferencesUtil().isPerfIndexDisabled()) {
                        
                        initialQuery = initialQuery.replaceAll("1337(?!17331)\\d+7331", "1");
                        LOGGER.log(LogLevel.CONSOLE_INFORM, "Calibrating indexes disabled, forcing to index [1]");
                    }
                    
                    LOGGER.log(
                        LogLevel.CONSOLE_INFORM,
                        "Normal strategy triggered with query [{}]",
                        () -> currentCallable
                            .getQuery()
                            .trim()
                            .replaceAll("1337(\\d*)7330%2b1", "$1")
                    );
                    
                    break;
                }
            }
            
            // End the job
            taskExecutor.shutdown();
            if (!taskExecutor.awaitTermination(15, TimeUnit.SECONDS)) {
                taskExecutor.shutdownNow();
            }
            
        } catch (InterruptedException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
            Thread.currentThread().interrupt();
            
        } catch (ExecutionException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
        }

        return initialQuery;
    }
}