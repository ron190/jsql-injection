package com.jsql.model.suspendable;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.suspendable.callable.CallablePageSource;
import com.jsql.model.suspendable.callable.ThreadFactoryCallable;

/**
 * Runnable class, search the correct number of fields in the SQL query.
 * Concurrent search with stop capability
 */
public class SuspendableGetIndexes extends AbstractSuspendable<String> {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
    public SuspendableGetIndexes(InjectionModel injectionModel) {
        super(injectionModel);
    }

    @Override
    public String run(Object... args) throws JSqlException {
        
        // Concurrent search
        ExecutorService taskExecutor;
        
        if (this.injectionModel.getMediatorUtils().getPreferencesUtil().isLimitingThreads()) {
            
            int countThreads = this.injectionModel.getMediatorUtils().getPreferencesUtil().countLimitingThreads();
            taskExecutor = Executors.newFixedThreadPool(countThreads, new ThreadFactoryCallable("CallableGetIndexes"));
            
        } else {
            
            taskExecutor = Executors.newCachedThreadPool(new ThreadFactoryCallable("CallableGetIndexes"));
        }
        
        CompletionService<CallablePageSource> taskCompletionService = new ExecutorCompletionService<>(taskExecutor);

        boolean isRequestFound = false;
        String initialQuery = StringUtils.EMPTY;
        int nbIndex;

        // SQL fields are built like 1337[index]7330+1
        // 7330+1 allows to exclude false positive when page contains injection URL
        // Search if the source contains 1337[index]7331
        for (nbIndex = 1 ; nbIndex <= 100 ; nbIndex++) {
            
            taskCompletionService.submit(
                new CallablePageSource(
                    this.injectionModel.getMediatorVendor().getVendor().instance().sqlIndices(nbIndex),
                    this.injectionModel,
                    "normal:index-" + nbIndex
                )
            );
        }
        
        nbIndex = 1;

        try {
            // Start from 10 to 100 requests
            while (nbIndex <= 100) {

                if (this.isSuspended()) {
                    throw new StoppedByUserSlidingException();
                }

                CallablePageSource currentCallable = taskCompletionService.take().get();
                nbIndex++;

                // Found a correct mark 1337[index]7331 in the source
                // TODO 1337 0%2b1
                if (Pattern.compile("(?s).*1337\\d+7331.*").matcher(currentCallable.getContent()).matches()) {
                    
                    this.injectionModel.getMediatorStrategy().getNormal().setSourceIndexesFound(currentCallable.getContent());
                    initialQuery = currentCallable.getUrl().replace("0%2b1", "1");
                    isRequestFound = true;
                    break;
                }
            }
            
            // End the job
            taskExecutor.shutdown();
            if (!taskExecutor.awaitTermination(15, TimeUnit.SECONDS)) {
                taskExecutor.shutdownNow();
            }
            
        } catch (InterruptedException | ExecutionException e) {
            
            LOGGER.error("Interruption while searching for injection indexes", e);
            Thread.currentThread().interrupt();
        }

        if (isRequestFound) {
            return initialQuery;
        }
        
        return StringUtils.EMPTY;
    }
}