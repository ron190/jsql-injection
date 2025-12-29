package com.jsql.model.injection.strategy.blind;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.InjectionFailureException;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.injection.strategy.blind.callable.CallableBlindBin;
import com.jsql.model.injection.strategy.blind.patch.Diff;
import com.jsql.model.injection.strategy.blind.patch.DiffMatchPatch;
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

/**
 * A blind attack class using concurrent threads.
 */
public class InjectionBlindBin extends AbstractInjectionMonobit<CallableBlindBin> {

    private static final Logger LOGGER = LogManager.getRootLogger();
    private static final int LOW = 0;
    private static final int HIGH = 127;

    private String sourceReferencePage;  // Source code of the TRUE web page (usually ?id=1)

    /**
     * List of string differences found in all the FALSE queries, compared
     * to the reference page. Each FALSE pages should contain
     * at least one same string, which shouldn't be present in all
     * the TRUE queries.
     */
    private List<Diff> falseDiffs = new ArrayList<>();
    private List<Diff> trueDiffs = new ArrayList<>();

    /**
     * Create blind attack initialization.
     * If every false diffs are not in true diffs and every true diffs are in
     * true diffs, then Blind attack is confirmed.
     */
    public InjectionBlindBin(InjectionModel injectionModel, BlindOperator blindOperator) {
        super(injectionModel, blindOperator);

        List<String> falsys = this.injectionModel.getMediatorVendor().getVendor().instance().getFalsyBin();
        if (falsys.isEmpty() || this.injectionModel.isStoppedByUser()) {
            return;
        }
        
        // Call the SQL request which must be TRUE (usually ?id=1)
        this.sourceReferencePage = this.callUrl(StringUtils.EMPTY, "bin#ref:"+ blindOperator.toString().toLowerCase());

        // Concurrent calls to the FALSE statements,
        // it will use inject() from the model
        ExecutorService taskExecutor = this.injectionModel.getMediatorUtils().getThreadUtil().getExecutor("CallableGetBlindBinTagFalse");
        Collection<CallableBlindBin> callablesFalsys = new ArrayList<>();
        for (String falsy: falsys) {
            callablesFalsys.add(new CallableBlindBin(
                falsy,
                injectionModel,
                this,
                blindOperator,
                -1, -1, -1,
                "bin#falsy"
            ));
        }
        
        // Delete junk from the results of FALSE statements,
        // keep only diffs found in each and every FALSE pages.
        // Allow the user to stop the loop
        try {
            List<Future<CallableBlindBin>> futuresFalsys = taskExecutor.invokeAll(callablesFalsys);
            this.injectionModel.getMediatorUtils().getThreadUtil().shutdown(taskExecutor);
            for (Future<CallableBlindBin> futureFalsy: futuresFalsys) {
                if (this.injectionModel.isStoppedByUser()) {
                    return;
                }
                if (this.falseDiffs.isEmpty()) {
                    this.falseDiffs = futureFalsy.get().getDiffsWithReference();  // Init diffs
                } else {
                    this.falseDiffs.retainAll(futureFalsy.get().getDiffsWithReference());  // Clean un-matching diffs
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
        
        this.cleanTrueDiffs(injectionModel, blindOperator);
    }

    private void cleanTrueDiffs(InjectionModel injectionModel, BlindOperator blindOperator) {
        ExecutorService taskExecutor = this.injectionModel.getMediatorUtils().getThreadUtil().getExecutor("CallableGetBlindBinTagTrue");
        Collection<CallableBlindBin> callablesTruthys = new ArrayList<>();
        List<String> truthys = this.injectionModel.getMediatorVendor().getVendor().instance().getTruthyBin();
        for (String truthy: truthys) {
            callablesTruthys.add(new CallableBlindBin(
                truthy,
                injectionModel,
                this,
                blindOperator,
                -1, -1, -1,
                "bin#truthy"
            ));
        }
        
        // Remove TRUE diffs in the FALSE diffs as FALSE statement shouldn't contain any TRUE diff.
        try {
            List<Future<CallableBlindBin>> futuresTruthys = taskExecutor.invokeAll(callablesTruthys);
            this.injectionModel.getMediatorUtils().getThreadUtil().shutdown(taskExecutor);
            for (Future<CallableBlindBin> futureTruthy: futuresTruthys) {
                if (this.injectionModel.isStoppedByUser()) {
                    return;
                }
                if (this.trueDiffs.isEmpty()) {
                    this.trueDiffs = futureTruthy.get().getDiffsWithReference();  // Init diffs
                } else {
                    this.trueDiffs.retainAll(futureTruthy.get().getDiffsWithReference());  // Clean un-matching diffs
                }
                this.falseDiffs.removeAll(futureTruthy.get().getDiffsWithReference());
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
        return blindTest.isTrue()
            // when insertionChar = true then pages ref == truthy == falsy == confirm => falsy cleaned empty, truthy with opcode EQUAL not reliable
            && this.trueDiffs.stream().anyMatch(diff -> !DiffMatchPatch.Operation.EQUAL.equals(diff.getOperation()))
            || this.falseDiffs.stream().anyMatch(diff -> !DiffMatchPatch.Operation.EQUAL.equals(diff.getOperation()));
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
                if (this.isCorruptOrElseNextChar(bytes, indexChar, countBadAsciiCode, currentCallable, low)) {
                    return;  // too many errors
                }
                low = InjectionBlindBin.LOW;
                high = InjectionBlindBin.HIGH;
            } else if (currentCallable.isTrue()) {  // current >= mid
                low = mid + 1;
            } else {  // current < mid
                high = mid - 1;
            }
        } else {
            low = InjectionBlindBin.LOW;
            high = InjectionBlindBin.HIGH;
            bytes.add(AbstractInjectionBit.getBitsUnset());
            indexChar.incrementAndGet();
        }

        mid = low + (high - low) / 2;
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

    private boolean isCorruptOrElseNextChar(List<char[]> bytes, AtomicInteger indexChar, AtomicInteger countBadAsciiCode, CallableBlindBin currentCallable, int low) {
        int currentLow = low;
        if (currentLow == 0 || currentLow == 127) {
            countBadAsciiCode.incrementAndGet();
        } else {
            currentLow = currentCallable.isTrue() ? currentLow : currentLow - 1;
        }
        char[] asciiCodeMask = bytes.get(currentCallable.getCurrentIndex() - 1);  // bits for current url
        this.setAsciiCodeMask(asciiCodeMask, currentLow);

        try {
            this.isCharCompleteWithCorruptCheck(bytes, countBadAsciiCode, currentCallable);
        } catch (InjectionFailureException e) {
            return true;
        }

        bytes.add(AbstractInjectionBit.getBitsUnset());
        indexChar.incrementAndGet();
        return false;
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

    public List<Diff> getTrueDiffs() {
        return this.trueDiffs;
    }
}
