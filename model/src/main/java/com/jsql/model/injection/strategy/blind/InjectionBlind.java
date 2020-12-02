package com.jsql.model.injection.strategy.blind;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.injection.strategy.blind.patch.Diff;
import com.jsql.model.suspendable.callable.ThreadFactoryCallable;

/**
 * A blind attack class using concurrent threads.
 */
public class InjectionBlind extends AbstractInjectionBoolean<CallableBlind> {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    // Source code of the TRUE web page (usually ?id=1)
    private String blankTrueMark;

    /**
     * List of string differences found in all the FALSE queries, compared
     * to the TRUE page (aka opcodes). Each FALSE pages should contain
     * at least one same string, which shouldn't be present in all
     * the TRUE queries.
     */
    private List<Diff> constantFalseMark = new ArrayList<>();

    /**
     * Create blind attack initialization.
     * If every false test are not in true mark and every true test are in
     * true test, then blind attack is confirmed.
     * @param blindMode
     */
    public InjectionBlind(InjectionModel injectionModel, BooleanMode blindMode) {
        
        super(injectionModel, blindMode);
        
        // No blind
        if (this.falseTest.isEmpty() || this.injectionModel.isStoppedByUser()) {
            
            return;
        }
        
        // Call the SQL request which must be TRUE (usually ?id=1)
        this.blankTrueMark = this.callUrl(StringUtils.EMPTY, "blind:set-true-mark");

        // Concurrent calls to the FALSE statements,
        // it will use inject() from the model
        ExecutorService taskExecutor;
        
        if (injectionModel.getMediatorUtils().getPreferencesUtil().isLimitingThreads()) {
            
            int countThreads = injectionModel.getMediatorUtils().getPreferencesUtil().countLimitingThreads();
            taskExecutor = Executors.newFixedThreadPool(countThreads, new ThreadFactoryCallable("CallableGetBlindTagFalse"));
            
        } else {
            
            taskExecutor = Executors.newCachedThreadPool(new ThreadFactoryCallable("CallableGetBlindTagFalse"));
        }
        
        Collection<CallableBlind> listCallableTagFalse = new ArrayList<>();
        
        for (String urlTest: this.falseTest) {
            
            listCallableTagFalse.add(new CallableBlind(urlTest, injectionModel, this, blindMode, "blind:false-mark"));
        }
        
        // Delete junk from the results of FALSE statements,
        // keep only opcodes found in each and every FALSE pages.
        // Allow the user to stop the loop
        try {
            List<Future<CallableBlind>> listTagFalse = taskExecutor.invokeAll(listCallableTagFalse);
            
            taskExecutor.shutdown();
            
            if (!taskExecutor.awaitTermination(15, TimeUnit.SECONDS)) {
                
                taskExecutor.shutdownNow();
            }
            
            this.constantFalseMark = listTagFalse.get(0).get().getOpcodes();
            
            for (Future<CallableBlind> falseMark: listTagFalse) {
                
                if (this.injectionModel.isStoppedByUser()) {
                    return;
                }
                
                this.constantFalseMark.retainAll(falseMark.get().getOpcodes());
            }
        } catch (ExecutionException e) {
            
            LOGGER.error("Searching fails for Blind False tags", e);
            
        } catch (InterruptedException e) {
            
            LOGGER.error("Interruption while searching for Blind False tags", e);
            Thread.currentThread().interrupt();
        }

        if (this.injectionModel.isStoppedByUser()) {
            return;
        }
        
        this.initializeTrueMarks(injectionModel, blindMode);
    }

    private void initializeTrueMarks(InjectionModel injectionModel, BooleanMode blindMode) {
        
        // Concurrent calls to the TRUE statements,
        // it will use inject() from the model.
        ExecutorService taskExecutor;
        
        if (injectionModel.getMediatorUtils().getPreferencesUtil().isLimitingThreads()) {
            
            int countThreads = injectionModel.getMediatorUtils().getPreferencesUtil().countLimitingThreads();
            taskExecutor = Executors.newFixedThreadPool(countThreads, new ThreadFactoryCallable("CallableGetBlindTagTrue"));
            
        } else {
            
            taskExecutor = Executors.newCachedThreadPool(new ThreadFactoryCallable("CallableGetBlindTagTrue"));
        }

        Collection<CallableBlind> listCallableTagTrue = new ArrayList<>();
        
        for (String urlTest: this.trueTest) {
            
            listCallableTagTrue.add(new CallableBlind(urlTest, injectionModel, this, blindMode, "blind:true-mark"));
        }
        
        // Remove TRUE opcodes in the FALSE opcodes, because
        // a significant FALSE statement shouldn't contain any TRUE opcode.
        // Allow the user to stop the loop.
        try {
            List<Future<CallableBlind>> listTagTrue = taskExecutor.invokeAll(listCallableTagTrue);
            
            taskExecutor.shutdown();
            if (!taskExecutor.awaitTermination(15, TimeUnit.SECONDS)) {
                
                taskExecutor.shutdownNow();
            }
        
            for (Future<CallableBlind> trueTag: listTagTrue) {
                
                if (this.injectionModel.isStoppedByUser()) {
                    return;
                }
                this.constantFalseMark.removeAll(trueTag.get().getOpcodes());
            }
            
        } catch (ExecutionException e) {
            
            LOGGER.error("Searching fails for Blind True tags", e);
            
        } catch (InterruptedException e) {
            
            LOGGER.error("Interruption while searching for Blind True tags", e);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public CallableBlind getCallableSizeTest(String string, int indexCharacter) {
        
        return new CallableBlind(string, indexCharacter, this.injectionModel, this, this.booleanMode, "blind:length-test");
    }

    @Override
    public CallableBlind getCallableBitTest(String string, int indexCharacter, int bit) {
        
        return new CallableBlind(string, indexCharacter, bit, this.injectionModel, this, this.booleanMode, "blind:bit-test");
    }

    @Override
    public boolean isInjectable() throws StoppedByUserSlidingException {
        
        if (this.injectionModel.isStoppedByUser()) {
            throw new StoppedByUserSlidingException();
        }
        
        CallableBlind blindTest = new CallableBlind(
            this.injectionModel.getMediatorVendor().getVendor().instance().sqlTestBooleanInitialization(),
            this.injectionModel,
            this,
            this.booleanMode,
            "blind:is-injectable"
        );
        
        try {
            blindTest.call();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return blindTest.isTrue() && !this.constantFalseMark.isEmpty();
    }

    @Override
    public String getInfoMessage() {
        
        return
            "Blind strategy: a request is true if the diff between"
            + " a correct page (e.g existing id) and current page"
            + " is not as the following: "
            + this.constantFalseMark
        ;
    }
    
    
    // Getter and setter

    public String getBlankTrueMark() {
        return this.blankTrueMark;
    }
    
    public List<Diff> getConstantFalseMark() {
        return this.constantFalseMark;
    }
}
