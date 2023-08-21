package com.jsql.model.injection.strategy.blind;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.injection.strategy.blind.patch.Diff;
import com.jsql.util.LogLevelUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class InjectionMultibit extends AbstractInjectionBoolean<CallableMultibit> {

    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    private String sourceRef;

    private List<Diff> diffsRefWithMultibitIds = new ArrayList<>();
    private final List<List<Diff>> multibitIds = new ArrayList<>();

    public InjectionMultibit(InjectionModel injectionModel, BooleanMode blindMode) {
        
        super(injectionModel, blindMode);
        
        if (this.injectionModel.isStoppedByUser()) {

            return;
        }

        this.sourceRef = this.callUrl("8", "multibit#ref");

        ExecutorService taskExecutor = this.injectionModel.getMediatorUtils().getThreadUtil().getExecutor("CallableGetMultibitIds");

        Collection<CallableMultibit> listCallableMultibitIds = new ArrayList<>();

        for (int i = 0; i < 8 ; i++) {

            listCallableMultibitIds.add(
                new CallableMultibit(
                    ""+i,
                    this.injectionModel,
                    this,
                    "multibit#ref~" + i
                )
            );
        }

        try {
            List<Future<CallableMultibit>> multibitIdRefs = taskExecutor.invokeAll(listCallableMultibitIds);

            taskExecutor.shutdown();

            if (!taskExecutor.awaitTermination(15, TimeUnit.SECONDS)) {

                taskExecutor.shutdownNow();
            }

            for (Future<CallableMultibit> multibitIdRef: multibitIdRefs) {

                List<Diff> opcodes = multibitIdRef.get().getOpcodes();
                if (this.diffsRefWithMultibitIds.isEmpty()) {
                    this.diffsRefWithMultibitIds = new ArrayList<>(opcodes);
                } else {
                    this.diffsRefWithMultibitIds.retainAll(opcodes);
                }
                multibitIds.add(opcodes);
            }

            for (List<Diff> multibitId : multibitIds) {

                multibitId.removeAll(this.diffsRefWithMultibitIds);
            }

        } catch (ExecutionException e) {

            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);

        } catch (InterruptedException e) {

            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public CallableMultibit getCallableMultibitTest(String sqlQuery, int indexCharacter, int block) {
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
    public CallableMultibit getCallableBitTest(String sqlQuery, int indexCharacter, int bit) {

        return null;
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
        
        return "- Strategy Multibit: page matching following Diffs converts to related 3 bits => " + this.multibitIds + "\n\n";
    }
    
    
    // Getter and setter

    public String getSourceRef() {
        return this.sourceRef;
    }

    public List<Diff> getDiffsRefWithMultibitIds() {
        return this.diffsRefWithMultibitIds;
    }

    public List<List<Diff>> getMultibitIds() {
        return multibitIds;
    }
}
