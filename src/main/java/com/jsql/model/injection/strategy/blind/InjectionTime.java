package com.jsql.model.injection.strategy.blind;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
     * Create time attack initialisation.
     * If every false requests are under 5 seconds and every true are below 5 seconds,
     * then time attack is confirmed.
     */
    public InjectionTime(InjectionModel injectionModel) {
        super(injectionModel);
        
        // No blind
        if (this.falseTest.length == 0) {
            return;
        }
        
        // Check if the user wants to stop the preparation
        if (this.injectionModel.isStoppedByUser()) {
            return;
        }

        /*
         *  Parallelize the call to the FALSE statements,
         *  it will use inject() from the model
         */
        ExecutorService executorTagFalse = Executors.newCachedThreadPool(new ThreadFactoryCallable("CallableGetTimeTagFalse"));
        Collection<CallableTime> listCallableTagFalse = new ArrayList<>();
        for (String urlTest: this.falseTest) {
            listCallableTagFalse.add(new CallableTime(urlTest, injectionModel, this));
        }
        
        // Begin the url requests
        List<Future<CallableTime>> listTagFalse = null;
        try {
            listTagFalse = executorTagFalse.invokeAll(listCallableTagFalse);
        } catch (InterruptedException e) {
            LOGGER.error("Interruption while checking Time False tags", e);
            Thread.currentThread().interrupt();
        }
        executorTagFalse.shutdown();

        /*
         * If one FALSE query makes less than X seconds,
         * then the test is a failure => exit
         * Allow the user to stop the loop
         */
        try {
            for (Future<CallableTime> tagFalse: listTagFalse) {
                if (this.injectionModel.isStoppedByUser()) {
                    return;
                }
                if (tagFalse.get().isTrue()) {
                    this.isTimeInjectable = false;
                    return;
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Interruption while checking Time injection", e);
            Thread.currentThread().interrupt();
        }

        /*
         *  Parallelize the call to the TRUE statements,
         *  it will use inject() from the model
         */
        ExecutorService executorTagTrue = Executors.newCachedThreadPool(new ThreadFactoryCallable("CallableGetTimeTagTrue"));
        Collection<CallableTime> listCallableTagTrue = new ArrayList<>();
        for (String urlTest: this.trueTest) {
            listCallableTagTrue.add(new CallableTime(urlTest, injectionModel, this));
        }
        
        // Begin the url requests
        List<Future<CallableTime>> listTagTrue = null;
        try {
            listTagTrue = executorTagTrue.invokeAll(listCallableTagTrue);
        } catch (InterruptedException e) {
            LOGGER.error("Interruption while checking Time True tags", e);
            Thread.currentThread().interrupt();
        }
        executorTagTrue.shutdown();

        /*
         * If one TRUE query makes more than X seconds,
         * then the test is a failure => exit.
         * Allow the user to stop the loop
         */
        try {
            for (Future<CallableTime> falseMark: listTagTrue) {
                if (this.injectionModel.isStoppedByUser()) {
                    return;
                }
                if (!falseMark.get().isTrue()) {
                    this.isTimeInjectable = false;
                    return;
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Interruption while checking Time injection", e);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public CallableTime getCallable(String string, int indexCharacter, boolean isTestingLength) {
        return new CallableTime(string, indexCharacter, isTestingLength, this.injectionModel, this);
    }

    @Override
    public CallableTime getCallable(String string, int indexCharacter, int bit) {
        return new CallableTime(string, indexCharacter, bit, this.injectionModel, this);
    }

    @Override
    public boolean isInjectable() throws StoppedByUserSlidingException {
        if (this.injectionModel.isStoppedByUser()) {
            throw new StoppedByUserSlidingException();
        }
        
        CallableTime blindTest = new CallableTime(this.injectionModel.getMediatorVendor().getVendor().instance().sqlTestBlindFirst(), this.injectionModel, this);
        try {
            blindTest.call();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return this.isTimeInjectable && blindTest.isTrue();
    }

    @Override
    public String getInfoMessage() {
        return "Time strategy: request is true if delay does not exceed 5 seconds.";
    }
    
}
