package com.jsql.model.injection.strategy.blind;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.injection.strategy.blind.callable.CallableTime;
import com.jsql.util.LogLevelUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Time attack using parallel threads.
 * Waiting time in seconds, response time exceeded means query is false.
 * Noting that sleep() functions will add up for each line from request.
 * A sleep time of 5 will be executed only if the SELECT returns exactly one line.
 */
public class InjectionTime extends AbstractInjectionMonobit<CallableTime> {

    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     *  Time based works by default, many tests will change it to false if it isn't confirmed.
     */
    private boolean isTimeInjectable = true;

    /**
     * Create time attack initialization.
     * If every false requests are under 5 seconds and every true are below 5 seconds,
     * then time attack is confirmed.
     */
    public InjectionTime(InjectionModel injectionModel, BlindOperator blindOperator) {
        super(injectionModel, blindOperator);

        List<String> falsys = this.injectionModel.getMediatorEngine().getEngine().instance().getFalsyBit();
        if (falsys.isEmpty() || this.injectionModel.isStoppedByUser()) {
            return;
        }

        // Concurrent calls to the FALSE statements,
        // it will use inject() from the model
        ExecutorService taskExecutor = this.injectionModel.getMediatorUtils().threadUtil().getExecutor("CallableGetTimeTagFalse");
        Collection<CallableTime> callablesFalsys = new ArrayList<>();
        for (String falsy: falsys) {
            callablesFalsys.add(new CallableTime(
                falsy,
                injectionModel,
                this,
                blindOperator,
                "time#falsy"
            ));
        }
        
        // If one FALSE query makes less than X seconds,
        // then the test is a failure => exit
        // Allow the user to stop the loop
        try {
            List<Future<CallableTime>> futuresFalsys = taskExecutor.invokeAll(callablesFalsys);
            this.injectionModel.getMediatorUtils().threadUtil().shutdown(taskExecutor);
            for (Future<CallableTime> futureFalsy: futuresFalsys) {
                if (this.injectionModel.isStoppedByUser()) {
                    return;
                }
                if (futureFalsy.get().isTrue()) {
                    this.isTimeInjectable = false;
                    return;
                }
            }
        } catch (ExecutionException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        } catch (InterruptedException e) {
            LOGGER.log(LogLevelUtil.IGNORE, e, e);
            Thread.currentThread().interrupt();
        }
        
        this.checkTruthys(blindOperator);
    }

    private void checkTruthys(BlindOperator blindOperator) {
        ExecutorService taskExecutor = this.injectionModel.getMediatorUtils().threadUtil().getExecutor("CallableGetTimeTagTrue");
        Collection<CallableTime> callablesTruthys = new ArrayList<>();
        List<String> truthys = this.injectionModel.getMediatorEngine().getEngine().instance().getTruthyBit();
        for (String truthy: truthys) {
            callablesTruthys.add(new CallableTime(
                truthy,
                this.injectionModel,
                this,
                blindOperator,
                "time#truthy"
            ));
        }

        // If one TRUE query makes more than X seconds then the test is a failure => exit
        try {
            List<Future<CallableTime>> futuresTruthys = taskExecutor.invokeAll(callablesTruthys);
            this.injectionModel.getMediatorUtils().threadUtil().shutdown(taskExecutor);
            for (Future<CallableTime> futureTruthy: futuresTruthys) {
                if (this.injectionModel.isStoppedByUser()) {
                    return;
                }
                if (!futureTruthy.get().isTrue()) {
                    this.isTimeInjectable = false;
                    return;
                }
            }
        } catch (ExecutionException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        } catch (InterruptedException e) {
            LOGGER.log(LogLevelUtil.IGNORE, e, e);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public CallableTime getCallableBitTest(String sqlQuery, int indexChar, int bit) {
        return new CallableTime(
            sqlQuery,
            indexChar,
            bit,
            this.injectionModel,
            this,
            this.blindOperator,
            "bit#" + indexChar + "~" + bit
        );
    }

    @Override
    public boolean isInjectable() throws StoppedByUserSlidingException {
        if (this.injectionModel.isStoppedByUser()) {
            throw new StoppedByUserSlidingException();
        }
        var callable = new CallableTime(
            this.injectionModel.getMediatorEngine().getEngine().instance().sqlBlindConfirm(),
            this.injectionModel,
            this,
            this.blindOperator,
            "time#confirm"
        );
        try {
            callable.call();
        } catch (Exception e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        }
        return this.isTimeInjectable && callable.isTrue();
    }

    public int getSleepTime() {
        return this.injectionModel.getMediatorUtils().preferencesUtil().isLimitingSleepTimeStrategy()
            ? this.injectionModel.getMediatorUtils().preferencesUtil().countSleepTimeStrategy()
            : 5;
    }

    @Override
    public String getInfoMessage() {
        return "- Strategy Time: query True when "+ this.getSleepTime() +"s delay\n\n";
    }
}
