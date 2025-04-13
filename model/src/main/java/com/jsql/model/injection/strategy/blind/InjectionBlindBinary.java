package com.jsql.model.injection.strategy.blind;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.InjectionFailureException;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.injection.strategy.StrategyBlindBinary;
import com.jsql.util.LogLevelUtil;
import name.fraser.neil.plaintext.diff_match_patch;
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

/**
 * A blind attack class using concurrent threads.
 */
public class InjectionBlindBinary extends AbstractInjectionMonobit<CallableBlindBinary> {

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
    private List<diff_match_patch.Diff> falseDiffs = new ArrayList<>();

    /**
     * Create blind attack initialization.
     * If every false diffs are not in true diffs and every true diffs are in
     * true diffs, then Blind attack is confirmed.
     */
    public InjectionBlindBinary(InjectionModel injectionModel, BinaryMode blindMode) {
        super(injectionModel, blindMode);
        
        // No blind
        if (this.falsyBinary.isEmpty() || this.injectionModel.isStoppedByUser()) {
            return;
        }
        
        // Call the SQL request which must be TRUE (usually ?id=1)
        this.sourceReferencePage = this.callUrl(StringUtils.EMPTY, "bin#ref");

        // Concurrent calls to the FALSE statements,
        // it will use inject() from the model
        ExecutorService taskExecutor = this.injectionModel.getMediatorUtils().getThreadUtil().getExecutor("CallableGetBlindTagFalse");
        Collection<CallableBlindBinary> callablesFalseTest = new ArrayList<>();
        for (String falseTest: this.falsyBinary) {
            callablesFalseTest.add(new CallableBlindBinary(
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
            List<Future<CallableBlindBinary>> futuresFalseTest = taskExecutor.invokeAll(callablesFalseTest);
            this.injectionModel.getMediatorUtils().getThreadUtil().shutdown(taskExecutor);
            for (Future<CallableBlindBinary> futureFalseTest: futuresFalseTest) {
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

    private void cleanTrueDiffs(InjectionModel injectionModel, BinaryMode blindMode) {
        // Concurrent calls to the TRUE statements,
        // it will use inject() from the model.
        ExecutorService taskExecutor = this.injectionModel.getMediatorUtils().getThreadUtil().getExecutor("CallableGetBlindTagTrue");
        Collection<CallableBlindBinary> callablesTrueTest = new ArrayList<>();
        for (String trueTest: this.truthyBinary) {
            callablesTrueTest.add(new CallableBlindBinary(
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
            List<Future<CallableBlindBinary>> futuresTrueTest = taskExecutor.invokeAll(callablesTrueTest);
            this.injectionModel.getMediatorUtils().getThreadUtil().shutdown(taskExecutor);
            for (Future<CallableBlindBinary> futureTrueTest: futuresTrueTest) {
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
    public CallableBlindBinary getCallableBitTest(String sqlQuery, int indexCharacter, int bit) {
        return null;  // unused
    }

    @Override
    public boolean isInjectable() throws StoppedByUserSlidingException {
        if (this.injectionModel.isStoppedByUser()) {
            throw new StoppedByUserSlidingException();
        }
        var blindTest = new CallableBlindBinary(
            this.injectionModel.getMediatorVendor().getVendor().instance().sqlTestBinaryInit(),
            this.injectionModel,
            this,
            this.binaryMode,
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
    public void initNextChars(
        String sqlQuery,
        List<char[]> bytes,
        AtomicInteger indexCharacter,
        CompletionService<CallableBlindBinary> taskCompletionService,
        AtomicInteger countTasksSubmitted,
        CallableBlindBinary currentCallable
    ) {
        var low = InjectionBlindBinary.LOW;
        var mid = InjectionBlindBinary.LOW + (InjectionBlindBinary.HIGH - InjectionBlindBinary.LOW) / 2;
        var high = InjectionBlindBinary.HIGH;
        AtomicInteger countBadAsciiCode = new AtomicInteger();

        if (currentCallable != null) {
            char[] asciiCodeMask = bytes.get(currentCallable.getCurrentIndex() - 1);  // bits for current url
            low = currentCallable.low;
            mid = currentCallable.mid;
            high = currentCallable.high;

            if (low == high) {
                low = currentCallable.isTrue() ? low : low - 1;
                asciiCodeMask[0] = StringUtils.leftPad(Integer.toBinaryString((char) low), 8, "0").charAt(0);
                asciiCodeMask[1] = StringUtils.leftPad(Integer.toBinaryString((char) low), 8, "0").charAt(1);
                asciiCodeMask[2] = StringUtils.leftPad(Integer.toBinaryString((char) low), 8, "0").charAt(2);
                asciiCodeMask[3] = StringUtils.leftPad(Integer.toBinaryString((char) low), 8, "0").charAt(3);
                asciiCodeMask[4] = StringUtils.leftPad(Integer.toBinaryString((char) low), 8, "0").charAt(4);
                asciiCodeMask[5] = StringUtils.leftPad(Integer.toBinaryString((char) low), 8, "0").charAt(5);
                asciiCodeMask[6] = StringUtils.leftPad(Integer.toBinaryString((char) low), 8, "0").charAt(6);
                asciiCodeMask[7] = StringUtils.leftPad(Integer.toBinaryString((char) low), 8, "0").charAt(7);
                try {
                    this.injectCharacter(bytes, countTasksSubmitted, countBadAsciiCode, currentCallable);
                } catch (InjectionFailureException e) {
                    return;
                }

                bytes.add(new char[]{ '0', 'x', 'x', 'x', 'x', 'x', 'x', 'x' });
                indexCharacter.incrementAndGet();
                low = InjectionBlindBinary.LOW;
                high = InjectionBlindBinary.HIGH;
            } else if (high < low) {
                low = currentCallable.isTrue() ? low : high;
                asciiCodeMask[0] = StringUtils.leftPad(Integer.toBinaryString((char) low), 8, "0").charAt(0);
                asciiCodeMask[1] = StringUtils.leftPad(Integer.toBinaryString((char) low), 8, "0").charAt(1);
                asciiCodeMask[2] = StringUtils.leftPad(Integer.toBinaryString((char) low), 8, "0").charAt(2);
                asciiCodeMask[3] = StringUtils.leftPad(Integer.toBinaryString((char) low), 8, "0").charAt(3);
                asciiCodeMask[4] = StringUtils.leftPad(Integer.toBinaryString((char) low), 8, "0").charAt(4);
                asciiCodeMask[5] = StringUtils.leftPad(Integer.toBinaryString((char) low), 8, "0").charAt(5);
                asciiCodeMask[6] = StringUtils.leftPad(Integer.toBinaryString((char) low), 8, "0").charAt(6);
                asciiCodeMask[7] = StringUtils.leftPad(Integer.toBinaryString((char) low), 8, "0").charAt(7);
                try {
                    this.injectCharacter(bytes, countTasksSubmitted, countBadAsciiCode, currentCallable);
                } catch (InjectionFailureException e) {
                    return;
                }

                bytes.add(new char[]{ '0', 'x', 'x', 'x', 'x', 'x', 'x', 'x' });
                indexCharacter.incrementAndGet();
                low = InjectionBlindBinary.LOW;
                high = InjectionBlindBinary.HIGH;
            } else if (currentCallable.isTrue()) {  // key < mid
                low = mid + 1;
            } else {  // key > mid
                high = mid - 1;
            }
            mid = low + (high - low) / 2;
        } else {
            bytes.add(new char[]{ '0', 'x', 'x', 'x', 'x', 'x', 'x', 'x' });
            indexCharacter.incrementAndGet();
        }

        taskCompletionService.submit(
            new CallableBlindBinary(
                sqlQuery,
                indexCharacter.get(),
                this.injectionModel,
                this,
                this.binaryMode,
                low,
                mid,
                high,
                "bit#" + indexCharacter + "~" //+ bit
            )
        );
        countTasksSubmitted.addAndGet(1);
    }

    @Override
    public char[] initBinaryMask(List<char[]> bytes, CallableBlindBinary currentCallable) {
        return bytes.get(currentCallable.getCurrentIndex() - 1);
    }

    @Override
    public String getInfoMessage() {
        return "- Strategy Blind binary: query True when Diffs are matching " + this.falseDiffs + "\n\n";
    }
    
    
    // Getter and setter

    public String getSourceReferencePage() {
        return this.sourceReferencePage;
    }
    
    public List<diff_match_patch.Diff> getFalseDiffs() {
        return this.falseDiffs;
    }
}
