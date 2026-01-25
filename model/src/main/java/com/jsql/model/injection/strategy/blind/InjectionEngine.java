package com.jsql.model.injection.strategy.blind;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.injection.strategy.blind.callable.AbstractCallableBit;
import com.jsql.model.injection.strategy.blind.callable.CallableEngine;
import com.jsql.model.injection.strategy.blind.patch.Diff;
import com.jsql.model.injection.engine.model.Engine;
import com.jsql.model.injection.engine.model.EngineYaml;
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

public class InjectionEngine {

    private static final Logger LOGGER = LogManager.getRootLogger();

    private String blankFalseMark;  // Source code of the FALSE web page (e.g. ?id=-123456789)

    private List<Diff> constantTrueMark = new ArrayList<>();

    protected final InjectionModel injectionModel;

    private final List<String> falsy;

    public InjectionEngine(InjectionModel injectionModel, String engineSpecificWithOperator, Engine engine) {
        this.injectionModel = injectionModel;

        List<String> truthy = this.injectionModel.getMediatorEngine().getEngine().instance().getTruthyBit();
        this.falsy = this.injectionModel.getMediatorEngine().getEngine().instance().getFalsyBit();
        
        // No blind
        if (truthy.isEmpty() || this.injectionModel.isStoppedByUser()) {
            return;
        }
        
        // Call the SQL request which must be FALSE (usually ?id=-123456879)
        this.blankFalseMark = this.callUrl(
            StringUtils.EMPTY,
            "vendor:" + engine
        );

        // Concurrent calls to the FALSE statements,
        // it will use inject() from the model
        ExecutorService taskExecutor = this.injectionModel.getMediatorUtils().threadUtil().getExecutor("CallableVendorTagTrue");
        Collection<CallableEngine> listCallableTagTrue = new ArrayList<>();
        for (String urlTest: truthy) {
            listCallableTagTrue.add(
                new CallableEngine(
                    engineSpecificWithOperator.replace(EngineYaml.TEST, urlTest),
                    this,
                    "vendor#true"
                )
            );
        }
        
        // Delete junk from the results of FALSE statements,
        // keep only opcodes found in each and every FALSE pages.
        // Allow the user to stop the loop
        try {
            List<Future<CallableEngine>> listTagTrue = taskExecutor.invokeAll(listCallableTagTrue);
            this.injectionModel.getMediatorUtils().threadUtil().shutdown(taskExecutor);
            
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
        
        this.initFalseMarks(engineSpecificWithOperator);
    }
    
    private void initFalseMarks(String engineSpecificWithMode) {
        // Concurrent calls to the TRUE statements,
        // it will use inject() from the model.
        ExecutorService taskExecutor = this.injectionModel.getMediatorUtils().threadUtil().getExecutor("CallableVendorTagFalse");
        Collection<CallableEngine> listCallableTagFalse = new ArrayList<>();
        for (String urlTest: this.falsy) {
            listCallableTagFalse.add(
                new CallableEngine(
                    engineSpecificWithMode.replace(EngineYaml.TEST, urlTest),
                    this,
                    "vendor#false"
                )
            );
        }
        
        // Remove TRUE opcodes in the FALSE opcodes, because
        // a significant FALSE statement shouldn't contain any TRUE opcode.
        // Allow the user to stop the loop.
        try {
            List<Future<CallableEngine>> listTagFalse = taskExecutor.invokeAll(listCallableTagFalse);
            this.injectionModel.getMediatorUtils().threadUtil().shutdown(taskExecutor);
            for (Future<CallableEngine> falseTag: listTagFalse) {
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

    public boolean isInjectable(String engineSpecificWithMode) throws StoppedByUserSlidingException {
        if (this.injectionModel.isStoppedByUser()) {
            throw new StoppedByUserSlidingException();
        }

        var blindTest = new CallableEngine(
            engineSpecificWithMode.replace(EngineYaml.TEST, this.injectionModel.getMediatorEngine().getEngine().instance().sqlBlindConfirm()),
            this,
            "vendor#confirm"
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
