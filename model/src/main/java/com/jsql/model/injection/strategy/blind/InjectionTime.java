package com.jsql.model.injection.strategy.blind;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.suspendable.callable.ThreadFactoryCallable;

/**
 * A time attack class using parallel threads.
 */
public class InjectionTime extends AbstractInjectionBoolean<CallableTime> {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    /**
     * Waiting time in seconds, if response time is above
     * then the SQL query is false.
     * Noting that sleep() functions will add up for each line from request.
     * A sleep time of 5 will be executed only if the SELECT returns exactly one line.
     */
    public static final long SLEEP_TIME = 5;

    /**
     *  Time based works by default, many tests will
     *  change it to false if it isn't confirmed.
     */
    private boolean isTimeInjectable = true;

    /**
     * Create time attack initialization.
     * If every false requests are under 5 seconds and every true are below 5 seconds,
     * then time attack is confirmed.
     */
    public InjectionTime(InjectionModel injectionModel, BooleanMode booleanMode) {
        
        super(injectionModel, booleanMode);
        
        // No blind
        if (this.falseTest.isEmpty() || this.injectionModel.isStoppedByUser()) {
            
            return;
        }

        /*
         *  Concurrent calls to the FALSE statements,
         *  it will use inject() from the model
         */
        ExecutorService executorTagFalse = Executors.newCachedThreadPool(new ThreadFactoryCallable("CallableGetTimeTagFalse"));
        Collection<CallableTime> listCallableTagFalse = new ArrayList<>();
        
        for (String urlTest: this.falseTest) {
            
            listCallableTagFalse.add(new CallableTime(urlTest, injectionModel, this, booleanMode));
        }
        
        /*
         * If one FALSE query makes less than X seconds,
         * then the test is a failure => exit
         * Allow the user to stop the loop
         */
        try {
            List<Future<CallableTime>> listTagFalse = executorTagFalse.invokeAll(listCallableTagFalse);
            
            executorTagFalse.shutdown();
            
            if (!executorTagFalse.awaitTermination(15, TimeUnit.SECONDS)) {
                
                executorTagFalse.shutdownNow();
            }
        
            for (Future<CallableTime> tagFalse: listTagFalse) {
                
                if (this.injectionModel.isStoppedByUser()) {
                    return;
                }
                
                if (tagFalse.get().isTrue()) {
                    
                    this.isTimeInjectable = false;
                    return;
                }
            }
        } catch (ExecutionException e) {
            
            LOGGER.error("Searching fails for Time False tags", e);
            
        } catch (InterruptedException e) {
            
            LOGGER.error("Interruption while searching for Time False tags", e);
            Thread.currentThread().interrupt();
        }
        
        this.initializeTrueMarks(booleanMode);
    }

    private void initializeTrueMarks(BooleanMode booleanMode) {
        
        // Concurrent calls to the TRUE statements,
        // it will use inject() from the model
        ExecutorService executorTagTrue = Executors.newCachedThreadPool(new ThreadFactoryCallable("CallableGetTimeTagTrue"));
        Collection<CallableTime> listCallableTagTrue = new ArrayList<>();
        
        for (String urlTest: this.trueTest) {
            
            listCallableTagTrue.add(new CallableTime(urlTest, this.injectionModel, this, booleanMode));
        }

        // If one TRUE query makes more than X seconds,
        // then the test is a failure => exit.
        // Allow the user to stop the loop
        try {
            List<Future<CallableTime>> listTagTrue = executorTagTrue.invokeAll(listCallableTagTrue);
            
            executorTagTrue.shutdown();
            if (!executorTagTrue.awaitTermination(15, TimeUnit.SECONDS)) {
                
                executorTagTrue.shutdownNow();
            }
        
            for (Future<CallableTime> falseMark: listTagTrue) {
                
                if (this.injectionModel.isStoppedByUser()) {
                    return;
                }
                
                if (!falseMark.get().isTrue()) {
                    
                    this.isTimeInjectable = false;
                    return;
                }
            }
            
        } catch (ExecutionException e) {
            
            LOGGER.error("Searching fails for Time True tags", e);
            
        } catch (InterruptedException e) {
            
            LOGGER.error("Interruption while searching for Time True tags", e);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public CallableTime getCallable(String string, int indexCharacter, boolean isTestingLength) {
        return new CallableTime(string, indexCharacter, isTestingLength, this.injectionModel, this, this.booleanMode);
    }

    @Override
    public CallableTime getCallable(String string, int indexCharacter, int bit) {
        return new CallableTime(string, indexCharacter, bit, this.injectionModel, this, this.booleanMode);
    }

    @Override
    public boolean isInjectable() throws StoppedByUserSlidingException {
        
        if (this.injectionModel.isStoppedByUser()) {
            throw new StoppedByUserSlidingException();
        }
        
        CallableTime timeTest = new CallableTime(
            this.injectionModel.getMediatorVendor().getVendor().instance().sqlTestBooleanInitialization(),
            this.injectionModel,
            this,
            this.booleanMode
        );
        
        try {
            timeTest.call();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return this.isTimeInjectable && timeTest.isTrue();
    }

    @Override
    public String getInfoMessage() {
        return "Time strategy: request is true if delay does not exceed 5 seconds.";
    }
}
