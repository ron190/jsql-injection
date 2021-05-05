package com.jsql.model.injection.strategy.blind;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.util.LogLevel;

/**
 * A time attack class using parallel threads.
 */
public class InjectionTime extends AbstractInjectionBoolean<CallableTime> {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * Waiting time in seconds, if response time is above
     * then the SQL query is false.
     * Noting that sleep() functions will add up for each line from request.
     * A sleep time of 5 will be executed only if the SELECT returns exactly one line.
     */

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

        // Concurrent calls to the FALSE statements,
        // it will use inject() from the model
        ExecutorService taskExecutor = this.injectionModel.getMediatorUtils().getThreadUtil().getExecutor("CallableGetTimeTagFalse");

        Collection<CallableTime> listCallableTagFalse = new ArrayList<>();
        
        for (String urlTest: this.falseTest) {
            
            listCallableTagFalse.add(new CallableTime(urlTest, injectionModel, this, booleanMode, "time#false"));
        }
        
        // If one FALSE query makes less than X seconds,
        // then the test is a failure => exit
        // Allow the user to stop the loop
        try {
            List<Future<CallableTime>> listTagFalse = taskExecutor.invokeAll(listCallableTagFalse);
            
            taskExecutor.shutdown();
            
            if (!taskExecutor.awaitTermination(15, TimeUnit.SECONDS)) {
                
                taskExecutor.shutdownNow();
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
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
            
        } catch (InterruptedException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
            Thread.currentThread().interrupt();
        }
        
        this.initializeTrueMarks(booleanMode);
    }

    private void initializeTrueMarks(BooleanMode booleanMode) {
        
        // Concurrent calls to the TRUE statements,
        // it will use inject() from the model
        ExecutorService taskExecutor = this.injectionModel.getMediatorUtils().getThreadUtil().getExecutor("CallableGetTimeTagTrue");

        Collection<CallableTime> listCallableTagTrue = new ArrayList<>();
        
        for (String urlTest: this.trueTest) {
            
            listCallableTagTrue.add(new CallableTime(urlTest, this.injectionModel, this, booleanMode, "time#true"));
        }

        // If one TRUE query makes more than X seconds,
        // then the test is a failure => exit.
        // Allow the user to stop the loop
        try {
            List<Future<CallableTime>> listTagTrue = taskExecutor.invokeAll(listCallableTagTrue);
            
            taskExecutor.shutdown();
            if (!taskExecutor.awaitTermination(15, TimeUnit.SECONDS)) {
                
                taskExecutor.shutdownNow();
            }
        
            for (Future<CallableTime> trueMark: listTagTrue) {
                
                if (this.injectionModel.isStoppedByUser()) {
                    return;
                }
                
                if (!trueMark.get().isTrue()) {
                    
                    this.isTimeInjectable = false;
                    return;
                }
            }
            
        } catch (ExecutionException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
            
        } catch (InterruptedException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public CallableTime getCallableSizeTest(String string, int indexCharacter) {
        
        return new CallableTime(string, indexCharacter, this.injectionModel, this, this.booleanMode, "time#size");
    }

    @Override
    public CallableTime getCallableBitTest(String string, int indexCharacter, int bit) {
        
        return new CallableTime(string, indexCharacter, bit, this.injectionModel, this, this.booleanMode, "time#bit");
    }

    @Override
    public boolean isInjectable() throws StoppedByUserSlidingException {
        
        if (this.injectionModel.isStoppedByUser()) {
            
            throw new StoppedByUserSlidingException();
        }
        
        var timeTest = new CallableTime(
            this.injectionModel.getMediatorVendor().getVendor().instance().sqlTestBooleanInitialization(),
            this.injectionModel,
            this,
            this.booleanMode,
            "time#confirm"
        );
        
        try {
            timeTest.call();
            
        } catch (Exception e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
        }

        return this.isTimeInjectable && timeTest.isTrue();
    }

    @Override
    public String getInfoMessage() {
        
        return "Time strategy: request is true if delay does not exceed 5 seconds.";
    }
}
