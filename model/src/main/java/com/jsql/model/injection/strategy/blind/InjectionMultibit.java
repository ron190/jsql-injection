package com.jsql.model.injection.strategy.blind;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.injection.strategy.blind.patch.Diff;
import com.jsql.util.LogLevelUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class InjectionMultibit extends AbstractInjectionBoolean<CallableMultibit> {

    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    private String sourceReference;

    private List<Diff> diffsCommonWithAllIds = new ArrayList<>();
    private final List<List<Diff>> diffsById = new ArrayList<>();

    public InjectionMultibit(InjectionModel injectionModel, BooleanMode blindMode) {
        
        super(injectionModel, blindMode);
        
        if (this.injectionModel.isStoppedByUser()) {

            return;
        }

        this.sourceReference = this.callUrl("8", "multibit#ref");

        ExecutorService taskExecutor = this.injectionModel.getMediatorUtils().getThreadUtil().getExecutor("CallableGetMultibitIds");

        Collection<CallableMultibit> callablesId = new ArrayList<>();

        for (int i = 0; i < 8 ; i++) {

            callablesId.add(
                new CallableMultibit(
                    ""+i,
                    this,
                    "multibit#ref~" + i
                )
            );
        }

        try {
            List<Future<CallableMultibit>> futuresId = taskExecutor.invokeAll(callablesId);

            taskExecutor.shutdown();

            if (!taskExecutor.awaitTermination(15, TimeUnit.SECONDS)) {

                taskExecutor.shutdownNow();
            }

            for (Future<CallableMultibit> futureId: futuresId) {

                List<Diff> diffsWithReference = futureId.get().getDiffsWithReference();
                if (this.diffsCommonWithAllIds.isEmpty()) {
                    this.diffsCommonWithAllIds = new ArrayList<>(diffsWithReference);
                } else {
                    this.diffsCommonWithAllIds.retainAll(diffsWithReference);
                }
                diffsById.add(diffsWithReference);
            }

            for (List<Diff> diffById : diffsById) {

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
    public void initializeNextCharacters(
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
    public char[] initializeBinaryMask(List<char[]> bytes, CallableMultibit currentCallable) {

        // Bits for current url
        char[] asciiCodeMask = bytes.get(currentCallable.getCurrentIndex() - 1);

        extractBitsFromBlock(currentCallable, asciiCodeMask);

        return asciiCodeMask;
    }

    /**
     * Extract 3 bits from callable for specific block
     */
    private void extractBitsFromBlock(CallableMultibit currentCallable, char[] bits) {

        if (currentCallable.block == 1) {
            convertIdPageToBits(currentCallable, bits, 0, 1, 2);
        } else if (currentCallable.block == 2) {
            convertIdPageToBits(currentCallable, bits, 3, 4, 5);
        } else if (currentCallable.block == 3) {
            convertIdPageToBits(currentCallable, bits, -1, 6,7);
        }
    }

    /**
     * Set bits by page id
     */
    private void convertIdPageToBits(CallableMultibit callable, char[] bits, int i1, int i2, int i3) {

        if (callable.idPage == 0) {
            if (i1 > -1) bits[i1] = '0';
            bits[i2] = '0';
            bits[i3] = '0';
        } else if (callable.idPage == 1) {
            if (i1 > -1) bits[i1] = '0';
            bits[i2] = '0';
            bits[i3] = '1';
        } else if (callable.idPage == 2) {
            if (i1 > -1) bits[i1] = '0';
            bits[i2] = '1';
            bits[i3] = '0';
        } else if (callable.idPage == 3) {
            if (i1 > -1) bits[i1] = '0';
            bits[i2] = '1';
            bits[i3] = '1';
        } else if (callable.idPage == 4) {
            if (i1 > -1) bits[i1] = '1';
            bits[i2] = '0';
            bits[i3] = '0';
        } else if (callable.idPage == 5) {
            if (i1 > -1) bits[i1] = '1';
            bits[i2] = '0';
            bits[i3] = '1';
        } else if (callable.idPage == 6) {
            if (i1 > -1) bits[i1] = '1';
            bits[i2] = '1';
            bits[i3] = '0';
        } else if (callable.idPage == 7) {
            if (i1 > -1) bits[i1] = '1';
            bits[i2] = '1';
            bits[i3] = '1';
        }
    }
    
    // Getter and setter

    public String getSourceReference() {
        return this.sourceReference;
    }

    public List<Diff> getDiffsCommonWithAllIds() {
        return this.diffsCommonWithAllIds;
    }

    public List<List<Diff>> getDiffsById() {
        return diffsById;
    }
}
