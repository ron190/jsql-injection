package com.jsql.model.injection.strategy.blind;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.jsql.model.MediatorModel;
import com.jsql.model.exception.StoppedByUserException;
import com.jsql.model.injection.strategy.blind.diff_match_patch.Diff;
import com.jsql.model.suspendable.ThreadFactoryCallable;

/**
 * A blind attack class using thread asynchronisation.
 */
public class ConcreteBlindInjection extends AbstractBlindInjection<CallableBlind> {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(ConcreteBlindInjection.class);

    /**
     * Source code of the TRUE web page (usually ?id=1).
     */
    private static String blankTrueMark;

    /**
     *  List of string differences found in all the FALSE queries, compared
     *  to the TRUE page (aka opcodes). Each FALSE pages should contain
     *  at least one same string, which shouldn't be present in all
     *  the TRUE queries.
     */
    private static List<Diff> constantFalseMark = new ArrayList<>();

    /**
     * Create blind attack initialisation.
     * If every false test are not in true mark and every true test are in
     * true test, then blind attack is confirmed.
     */
    public ConcreteBlindInjection() {
        // No blind
        if (this.falseTest.length == 0) {
            return;
        }
        
        // Call the SQL request which must be TRUE (usually ?id=1)
        ConcreteBlindInjection.blankTrueMark = ConcreteBlindInjection.callUrl("");

        // Check if the user wants to stop the preparation
        if (MediatorModel.model().isStoppedByUser()) {
            return;
        }

        /*
         *  Parallelize the call to the FALSE statements,
         *  it will use inject() from the model
         */
        ExecutorService executorTagFalse = Executors.newCachedThreadPool(new ThreadFactoryCallable("CallableGetBlindTagFalse"));
        Collection<CallableBlind> listCallableTagFalse = new ArrayList<>();
        for (String urlTest: this.falseTest) {
            listCallableTagFalse.add(new CallableBlind(urlTest));
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
            
            constantFalseMark = listTagFalse.get(0).get().getOpcodes();
            for (Future<CallableBlind> falseMark: listTagFalse) {
                if (MediatorModel.model().isStoppedByUser()) {
                    return;
                }
                constantFalseMark.retainAll(falseMark.get().getOpcodes());
            }
        } catch (ExecutionException e) {
            LOGGER.error("Searching fails for Blind False tags", e);
        } catch (InterruptedException e) {
            LOGGER.error("Interruption while searching for Blind False tags", e);
            Thread.currentThread().interrupt();
        }

        if (MediatorModel.model().isStoppedByUser()) {
            return;
        }

        /*
         *  Parallelize the call to the TRUE statements,
         *  it will use inject() from the model.
         */
        ExecutorService executorTagTrue = Executors.newCachedThreadPool(new ThreadFactoryCallable("CallableGetBlindTagTrue"));
        Collection<CallableBlind> listCallableTagTrue = new ArrayList<>();
        for (String urlTest: trueTest) {
            listCallableTagTrue.add(new CallableBlind(urlTest));
        }
        
        // Begin the url requests
        List<Future<CallableBlind>> listTagTrue = null;
        try {
            listTagTrue = executorTagTrue.invokeAll(listCallableTagTrue);
        } catch (InterruptedException e) {
            LOGGER.error(e, e);
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
                if (MediatorModel.model().isStoppedByUser()) {
                    return;
                }
                ConcreteBlindInjection.constantFalseMark.removeAll(trueTag.get().getOpcodes());
            }
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error(e, e);
        }
    }

    @Override
    public CallableBlind getCallable(String string, int indexCharacter, boolean isTestingLength) {
        return new CallableBlind(string, indexCharacter, isTestingLength);
    }

    @Override
    public CallableBlind getCallable(String string, int indexCharacter, int bit) {
        return new CallableBlind(string, indexCharacter, bit);
    }

    @Override
    public boolean isInjectable() throws StoppedByUserException {
        if (MediatorModel.model().isStoppedByUser()) {
            throw new StoppedByUserException();
        }
        
        if (MediatorModel.model().vendor.instance().sqlTestBlindFirst() == null) {
            return false;
        }
        
        CallableBlind blindTest = new CallableBlind(MediatorModel.model().vendor.instance().sqlTestBlindFirst());
        try {
            blindTest.call();
        } catch (Exception e) {
            LOGGER.error(e, e);
        }

        return blindTest.isTrue() && !ConcreteBlindInjection.constantFalseMark.isEmpty();
    }

    @Override
    public String getInfoMessage() {
        return 
            "A blind SQL request is true if the diff between "
            + "a correct page (e.g existing id) and current page "
            + "is not as the following: "
            + ConcreteBlindInjection.constantFalseMark + "\n"
        ;
    }

    /**
     * Get source code of the TRUE web page.
     * @return Source code in HTML
     */
    public static String getBlankTrueMark() {
        return blankTrueMark;
    }
    
    /**
     *  Get False Marks.
     *  @return False marks
     */
    public static List<Diff> getConstantFalseMark() {
        return constantFalseMark;
    }
}
