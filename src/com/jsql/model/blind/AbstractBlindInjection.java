package com.jsql.model.blind;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.model.bean.Request;
import com.jsql.model.injection.AbstractSuspendable;
import com.jsql.model.injection.MediatorModel;

/**
 *
 */
public abstract class AbstractBlindInjection<T extends CallableAbstractBlind<T>> {
    /**
     * Every FALSE SQL statements will be checked,
     * more statements means a more robust application
     */
    protected String[] falseTest = MediatorModel.model().sqlStrategy.getListFalseTest();

    /**
     * Every TRUE SQL statements will be checked,
     * more statements means a more robust application
     */
    protected String[] trueTest = MediatorModel.model().sqlStrategy.getListTrueTest();

    /**
     * Constant linked to a URL, true if that url
     * checks the end of the SQL result, false otherwise.
     */
    protected static final boolean ISLENGTHTEST = true;
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(AbstractBlindInjection.class);

    /**
     * Process the whole blind injection, character by character, bit by bit.
     * @param inj SQL query
     * @param stoppable Action a user can stop
     * @return Final string: SQLiABCDEF...
     * @throws StoppableException
     */
    public String inject(String inj, AbstractSuspendable stoppable) throws StoppableException {
        /**
         *  List of the characters, each one represented by an array of 8 bits
         *  e.g SQLi: bytes[0] => 01010011:S, bytes[1] => 01010001:Q ...
         */
        List<char[]> bytes = new ArrayList<char[]>();
        // Cursor for current character position
        int indexCharacter = 0;

        // Parallelize the URL requests
        ExecutorService taskExecutor = Executors.newFixedThreadPool(150);
        CompletionService<T> taskCompletionService = new ExecutorCompletionService<T>(taskExecutor);

        // Send the first binary question: is the SQL result empty?
        taskCompletionService.submit(getCallable(inj, 0, ISLENGTHTEST));
        // Increment the number of active tasks
        int submittedTasks = 1;

        /*
         * Process the job until there is no more active task,
         * in other word until all HTTP requests are done
         */
        while (submittedTasks > 0) {
            // stop/pause/resume if user needs that
            if (stoppable.stopOrPause()) {
                taskExecutor.shutdown();

                // Wait for termination
                boolean success = false;
                try {
                    success = taskExecutor.awaitTermination(0, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    LOGGER.error(e, e);
                }
                if (!success) {
                    // awaitTermination timed out, interrupt everyone
                    taskExecutor.shutdownNow();
                }

                throw new StoppableException();
            }
            try {
                // The URL call is done, bring back the finished task
                T currentCallable = taskCompletionService.take().get();
                // One task has just ended, decrease active tasks by 1
                submittedTasks--;
                /*
                 * If SQL result is not empty, then add a new unknown character,
                 * and define a new array of 8 undefined bit.
                 * Then add a new length verification, and all 8 bits
                 * requests for that new character.
                 */
                if (currentCallable.isLengthTest()) {
                    if (currentCallable.isTrue()) {
                        indexCharacter++;
                        // New undefined bits of the next character
                        bytes.add(new char[]{'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x'});
                        // Test if it's the end of the line
                        taskCompletionService.submit(getCallable(inj, indexCharacter, ISLENGTHTEST));
                        // Test the 8 bits for the next character, save its position and current bit for later
                        for (int bit: new int[]{1, 2, 4, 8, 16, 32, 64, 128}) {
                            taskCompletionService.submit(getCallable(inj, indexCharacter, bit));
                        }
                        // Add all 9 new tasks
                        submittedTasks += 9;
                    }
                /*
                 * Process the url that has just checked a bit,
                 * Retrieve the bits for that character, and
                 * change the bit from undefined to 0 or 1
                 */
                } else {
                    // The bits linked to the url
                    char[] e = bytes.get(currentCallable.getCurrentIndex() - 1);
                    // Define the bit
                    e[(int) (8 - (Math.log(2) + Math.log(currentCallable.getCurrentBit())) / Math.log(2)) ] = currentCallable.isTrue() ? '1' : '0';

                    /*
                     * Inform the View if a array of bits is complete, else nothing #Need fix
                     */
                    try {
                        int charCode = Integer.parseInt(new String(e), 2);
                        String str = Character.toString((char) charCode);

                        Request interaction = new Request();
                        interaction.setMessage("MessageBinary");
                        interaction.setParameters("\t" + new String(e) + "=" + str);
                        MediatorModel.model().interact(interaction);
                    // byte string not fully constructed : 0x1x010x
                    } catch (NumberFormatException err) {
                        // Ignore
                    }
                }
            } catch (InterruptedException e) {
                LOGGER.error(e, e);
            } catch (ExecutionException e) {
                LOGGER.error(e, e);
            }
        }

        // End the job
        try {
            taskExecutor.shutdown();
            taskExecutor.awaitTermination(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOGGER.error(e, e);
        }

        // Build the complete final string from array of bits
        String result = "";
        for (char[] c: bytes) {
            int charCode = Integer.parseInt(new String(c), 2);
            String str = Character.toString((char) charCode);
            result += str;
        }
        
        // System.out.println("nb caractères: "+indexChar);
        // System.out.println("résultat: "+result);
        // Return the final string
        return result;
    }

    /**
     * Run a HTTP call via the model.
     * @param urlString URL to inject
     * @return Source code
     */
    public static String callUrl(String urlString) {
        return MediatorModel.model().inject(MediatorModel.model().insertionCharacter + urlString);
    }
    
    /**
     * 
     * @param string
     * @param indexCharacter
     * @param isLengthTest
     * @return
     */
    public abstract T getCallable(String string, int indexCharacter, boolean isLengthTest);
    
    /**
     * 
     * @param string
     * @param indexCharacter
     * @param bit
     * @return
     */
    public abstract T getCallable(String string, int indexCharacter, int bit);
    
    /**
     * Start one test to verify if blind works.
     * @return true if blind method is confirmed
     * @throws PreparationException
     */
    public abstract boolean isInjectable() throws PreparationException;
    
    /**
     * Display a message to explain how is blid/time working.
     * @return
     */
    public abstract String getInfoMessage();
}
