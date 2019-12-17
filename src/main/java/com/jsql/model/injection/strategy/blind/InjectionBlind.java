package com.jsql.model.injection.strategy.blind;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.injection.strategy.blind.patch.Diff;
import com.jsql.model.suspendable.callable.ThreadFactoryCallable;

/**
 * A blind attack class using thread asynchronization.
 */
public class InjectionBlind extends AbstractInjectionBoolean<CallableBlind> {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    /**
     * Source code of the TRUE web page (usually ?id=1).
     */
    private String blankTrueMark;

    /**
     *  List of string differences found in all the FALSE queries, compared
     *  to the TRUE page (aka opcodes). Each FALSE pages should contain
     *  at least one same string, which shouldn't be present in all
     *  the TRUE queries.
     */
    private List<Diff> constantFalseMark = new ArrayList<>();

    /**
     * Create blind attack initialization.
     * If every false test are not in true mark and every true test are in
     * true test, then blind attack is confirmed.
     */
    public InjectionBlind(InjectionModel injectionModel) {
        super(injectionModel);
        
        // No blind
        if (this.falseTest.length == 0) {
            return;
        }
        
        // Call the SQL request which must be TRUE (usually ?id=1)
        this.blankTrueMark = this.callUrl("");

        // Check if the user wants to stop the preparation
        if (this.injectionModel.isStoppedByUser()) {
            return;
        }

        /*
         *  Parallelize the call to the FALSE statements,
         *  it will use inject() from the model
         */
        ExecutorService executorTagFalse = Executors.newCachedThreadPool(new ThreadFactoryCallable("CallableGetBlindTagFalse"));
        Collection<CallableBlind> listCallableTagFalse = new ArrayList<>();
        for (String urlTest: this.falseTest) {
            listCallableTagFalse.add(new CallableBlind(urlTest, injectionModel, this));
        }
        
        /*
         * Delete junk from the results of FALSE statements,
         * keep only opcodes found in each and every FALSE pages.
         * Allow the user to stop the loop
         */
        try {
            // Begin the url requests
            List<Future<CallableBlind>> listTagFalse = null;
            listTagFalse = executorTagFalse.invokeAll(listCallableTagFalse);
            executorTagFalse.shutdown();
            
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

        /*
         *  Parallelize the call to the TRUE statements,
         *  it will use inject() from the model.
         */
        ExecutorService executorTagTrue = Executors.newCachedThreadPool(new ThreadFactoryCallable("CallableGetBlindTagTrue"));
        Collection<CallableBlind> listCallableTagTrue = new ArrayList<>();
        for (String urlTest: this.trueTest) {
            listCallableTagTrue.add(new CallableBlind(urlTest, injectionModel, this));
        }
        
        // Begin the url requests
        List<Future<CallableBlind>> listTagTrue = null;
        try {
            listTagTrue = executorTagTrue.invokeAll(listCallableTagTrue);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        executorTagTrue.shutdown();

        /*
         * Remove TRUE opcodes in the FALSE opcodes, because
         * a significant FALSE statement shouldn't contain any TRUE opcode.
         * Allow the user to stop the loop.
         */
        try {
            for (Future<CallableBlind> trueTag: listTagTrue) {
                if (this.injectionModel.isStoppedByUser()) {
                    return;
                }
                this.constantFalseMark.removeAll(trueTag.get().getOpcodes());
            }
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public CallableBlind getCallable(String string, int indexCharacter, boolean isTestingLength) {
        return new CallableBlind(string, indexCharacter, isTestingLength, this.injectionModel, this);
    }

    @Override
    public CallableBlind getCallable(String string, int indexCharacter, int bit) {
        return new CallableBlind(string, indexCharacter, bit, this.injectionModel, this);
    }

    @Override
    public boolean isInjectable() throws StoppedByUserSlidingException {
        if (this.injectionModel.isStoppedByUser()) {
            throw new StoppedByUserSlidingException();
        }
        
        CallableBlind blindTest = new CallableBlind(this.injectionModel.mediatorVendor.getVendor().instance().sqlTestBlindFirst(), this.injectionModel, this);
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
            "Blind strategy: a request is true if the diff between "
            + "a correct page (e.g existing id) and current page "
            + "is not as the following: "
            + this.constantFalseMark
        ;
    }

    /**
     * Get source code of the TRUE web page.
     * @return Source code in HTML
     */
    public String getBlankTrueMark() {
        return this.blankTrueMark;
    }
    
    /**
     *  Get False Marks.
     *  @return False marks
     */
    public List<Diff> getConstantFalseMark() {
        return this.constantFalseMark;
    }
    
}
