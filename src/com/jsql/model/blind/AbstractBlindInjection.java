package com.jsql.model.blind;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.model.Interruptable;
import com.jsql.model.Stoppable;
import com.jsql.model.bean.Request;
import com.jsql.view.GUIMediator;

public abstract class AbstractBlindInjection {
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
        ExecutorService taskExecutor = Executors.newFixedThreadPool(150);
        CompletionService<IBlindCallable> taskCompletionService = new ExecutorCompletionService<IBlindCallable>(taskExecutor);

        // Send the first binary question: is the SQL result empty?
        taskCompletionService.submit(getCallable(inj, 0, IS_LENGTH_TEST)/*new T("+and+char_length("+inj+")>0--+", IS_LENGTH_TEST)*/);
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
            	IBlindCallable currentCallable = taskCompletionService.take().get();
                // One task has just ended, decrease active tasks by 1
                submittedTasks--;
                /**
                 * If SQL result is not empty, then add a new unknown character,
                 * and define a new array of 8 undefined bit.
                 * Then add a new length verification, and all 8 bit requests for that new character
                 */
                if(currentCallable.getisLengthTest()){
                    if( currentCallable.isTrue() ){
                        indexCharacter++;
                        // New undefined bits of the next character
                        bytes.add(new char[]{'x','x','x','x','x','x','x','x'});
                        // Test if it's the end of the line
                        taskCompletionService.submit(getCallable(inj, indexCharacter, IS_LENGTH_TEST));
                        // Test the 8 bits for the next character, save its position and current bit for later
                        for(int bit: new int[]{1,2,4,8,16,32,64,128}){
                            taskCompletionService.submit(getCallable(inj, indexCharacter, bit));
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
                    char[] e = bytes.get(currentCallable.getCurrentIndex()-1);
                    // Define the bit
                    e[(int) (8 - ( Math.log(2)+Math.log(currentCallable.getCurrentBit()) )/Math.log(2)) ] = currentCallable.isTrue() ? '1' : '0';

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

	public abstract Callable<IBlindCallable> getCallable(String string, int indexCharacter, boolean iS_LENGTH_TEST);
	public abstract Callable<IBlindCallable> getCallable(String string, int indexCharacter, int bit);
	public abstract boolean isInjectable() throws PreparationException;
}
