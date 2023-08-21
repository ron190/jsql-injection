package com.jsql.model.injection.strategy.blind;

import com.jsql.model.InjectionModel;
import com.jsql.model.accessible.DataAccess;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.model.exception.InjectionFailureException;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.suspendable.AbstractSuspendable;
import com.jsql.util.LogLevelUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractInjectionBoolean<T extends AbstractCallableBoolean<T>> {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    // Every FALSE SQL statements will be checked,
    // more statements means a more robust application
    protected final List<String> falseTest;
    
    // Every TRUE SQL statements will be checked,
    // more statements means a more robust application
    protected final List<String> trueTest;
    
    public enum BooleanMode {
        AND, OR, STACKED
    }
    
    protected final InjectionModel injectionModel;
    
    protected final BooleanMode booleanMode;
    
    protected AbstractInjectionBoolean(InjectionModel injectionModel, BooleanMode booleanMode) {
        
        this.injectionModel = injectionModel;
        this.booleanMode = booleanMode;

        this.falseTest = this.injectionModel.getMediatorVendor().getVendor().instance().getListFalseTest();
        this.trueTest = this.injectionModel.getMediatorVendor().getVendor().instance().getListTrueTest();
    }
    
    public abstract T getCallableBitTest(String sqlQuery, int indexCharacter, int bit);

    public abstract T getCallableMultibitTest(String sqlQuery, int indexCharacter, int block);

    /**
     * Start one test to verify if boolean works.
     * @return true if boolean method is confirmed
     */
    public abstract boolean isInjectable() throws StoppedByUserSlidingException;
    
    /**
     * Display a message to explain how is blind/time working.
     * @return
     */
    public abstract String getInfoMessage();

    /**
     * Process the whole boolean injection, character by character, bit by bit.
     * @param sqlQuery SQL query
     * @param suspendable Action a user can stop
     * @return Final string: SQLiABCDEF...
     */
    public String inject(String sqlQuery, AbstractSuspendable suspendable) throws StoppedByUserSlidingException {

        // List of the characters, each one represented by an array of 8 bits
        // e.g SQLi: bytes[0] => 01010011:S, bytes[1] => 01010001:Q ...
        List<char[]> bytes = new ArrayList<>();
        
        // Cursor for current character position
        var indexCharacter = new AtomicInteger(0);

        // Concurrent URL requests
        ExecutorService taskExecutor = this.injectionModel.getMediatorUtils().getThreadUtil().getExecutor("CallableAbstractBoolean");
        
        CompletionService<T> taskCompletionService = new ExecutorCompletionService<>(taskExecutor);

        var countTasksSubmitted = new AtomicInteger(0);
        var countBadAsciiCode = new AtomicInteger(0);

        if (this instanceof InjectionMultibit) {
            this.initializeNextMultibitCharacters(sqlQuery, bytes, indexCharacter, taskCompletionService, countTasksSubmitted);
        } else {
            this.initializeNextCharacters(sqlQuery, bytes, indexCharacter, taskCompletionService, countTasksSubmitted);
        }

        // Process the job until there is no more active task,
        // in other word until all HTTP requests are done
        while (countTasksSubmitted.get() > 0) {
            
            if (suspendable.isSuspended()) {
                
                String result = this.stop(bytes, taskExecutor);
                throw new StoppedByUserSlidingException(result);
            }
            
            try {
                // The URL call is done, bring back the finished task
                var currentCallable = taskCompletionService.take().get();
                
                // One task has just ended, decrease active tasks by 1
                countTasksSubmitted.decrementAndGet();
                
                // If SQL result is not empty, then add a new unknown character,
                // and define a new array of 8 undefined bit.
                // Then add 8 bits requests for that new character.
                this.injectCharacter(bytes, countTasksSubmitted, countBadAsciiCode, currentCallable);
                if (currentCallable.isMultibit()) {
                    this.initializeNextMultibitCharacters(sqlQuery, bytes, indexCharacter, taskCompletionService, countTasksSubmitted);
                } else {
                    this.initializeNextCharacters(sqlQuery, bytes, indexCharacter, taskCompletionService, countTasksSubmitted);
                }

                String result = convert(bytes);
                if (result.matches("(?s).*"+ DataAccess.TRAIL_RGX +".*")) {
                    countTasksSubmitted.set(0);
                    break;
                }
                
            } catch (InterruptedException e) {
                
                LOGGER.log(LogLevelUtil.IGNORE, e, e);
                Thread.currentThread().interrupt();
                
            } catch (ExecutionException e) {
                
                LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
                
            } catch (InjectionFailureException e) {
                
                LOGGER.log(LogLevelUtil.CONSOLE_ERROR, e.getMessage());
                break;
            }
        }

        return this.stop(bytes, taskExecutor);
    }

    private static String convert(List<char[]> bytes) {

        var result = new StringBuilder();

        for (char[] c: bytes) {

            try {
                var charCode = Integer.parseInt(new String(c), 2);
                var str = Character.toString((char) charCode);
                result.append(str);

            } catch (NumberFormatException err) {
                // Byte string not fully constructed : 0x1x010x
                // Ignore
            }
        }
        return result.toString();
    }

    private void injectCharacter(List<char[]> bytes, AtomicInteger countTasksSubmitted, AtomicInteger countBadAsciiCode, T currentCallable) throws InjectionFailureException {
        
        // Process url that has just checked one bit, convert bits to chars,
        // and change current bit from undefined to 0 or 1
        
        char[] asciiCodeMask = this.initializeBinaryMask(bytes, currentCallable);
        
        var asciiCodeBinary = new String(asciiCodeMask);
        
        // Inform the View if bits array is complete, else nothing #Need fix
        if (asciiCodeBinary.matches("^[01]{8}$")) {
            
            var asciiCode = Integer.parseInt(asciiCodeBinary, 2);
            
            // Stop if many 11111111, 01111111 or 00000000
            if (asciiCode == 255 || asciiCode == 127 || asciiCode == 0) {
                
                if (
                    countTasksSubmitted.get() != 0
                    && countBadAsciiCode.get() > 9
                    && (countBadAsciiCode.get() * 100 / countTasksSubmitted.get()) > 50
                ) {
                    throw new InjectionFailureException("Boolean false positives spotted, stopping...");
                }
                
                countBadAsciiCode.incrementAndGet();
            }

            var charText = Character.toString((char) asciiCode);
            currentCallable.charText = charText;
            
            var interaction = new Request();
            interaction.setMessage(Interaction.MESSAGE_BINARY);
            interaction.setParameters(
                asciiCodeBinary
                + "="
                + charText
                .replace("\\n", "\\\\\\n")
                .replace("\\r", "\\\\\\r")
                .replace("\\t", "\\\\\\t")
            );
            this.injectionModel.sendToViews(interaction);
        }
    }

    private void initializeNextMultibitCharacters(
        String sqlQuery,
        List<char[]> bytes,
        AtomicInteger indexCharacter,
        CompletionService<T> taskCompletionService,
        AtomicInteger countTasksSubmitted
    ) {
        indexCharacter.incrementAndGet();

        bytes.add(new char[]{ '0', 'x', 'x', 'x', 'x', 'x', 'x', 'x' });

        for (int block: new int[]{ 1, 2, 3 }) {

            taskCompletionService.submit(
                this.getCallableMultibitTest(
                    sqlQuery,
                    indexCharacter.get(),
                    block
                )
            );
        }

        // Add 9 new tasks
        countTasksSubmitted.addAndGet(3);
    }

    private void initializeNextCharacters(
        String sqlQuery,
        List<char[]> bytes,
        AtomicInteger indexCharacter,
        CompletionService<T> taskCompletionService,
        AtomicInteger countTasksSubmitted
    ) {
        indexCharacter.incrementAndGet();
        
        // New undefined bits of the next character
        // Chars all have the last bit set to 0 in Ascii table
        bytes.add(new char[]{ '0', 'x', 'x', 'x', 'x', 'x', 'x', 'x' });
        
        // Test the 8 bits for the next character, save its position and current bit for later
        // Ignore last bit 128 and only check for first seven bits
        for (int bit: new int[]{ 1, 2, 4, 8, 16, 32, 64 }) {
            
            taskCompletionService.submit(
                this.getCallableBitTest(
                    sqlQuery,
                    indexCharacter.get(),
                    bit
                )
            );
        }
        
        countTasksSubmitted.addAndGet(7);
    }

    private char[] initializeBinaryMask(List<char[]> bytes, T currentCallable) {

        if (currentCallable.isMultibit()) {

            // Bits for current url
            char[] asciiCodeMask = bytes.get(currentCallable.getCurrentIndex() - 1);

            extractBitsFromBlock(currentCallable, asciiCodeMask);

            return asciiCodeMask;

        } else {

            // Bits for current url
            char[] asciiCodeMask = bytes.get(currentCallable.getCurrentIndex() - 1);

            int positionInMask = (int) (
                8 - (Math.log(2) + Math.log(currentCallable.getCurrentBit()))
                / Math.log(2)
            );

            // Set current bit
            asciiCodeMask[positionInMask] = currentCallable.isTrue() ? '1' : '0';

            return asciiCodeMask;
        }
    }

    /**
     * Extract 3 bits from callable for specific block
     */
    private void extractBitsFromBlock(T currentCallable, char[] bits) {

        if (currentCallable.block == 1) {
            convertIdPageToBits(currentCallable, bits, 0, 1, 2);
        } else if (currentCallable.block == 2) {
            convertIdPageToBits(currentCallable, bits, 3, 4, 5);
        } else if (currentCallable.block == 3) {
            convertIdPageToBits(currentCallable, bits, -1, 6,7);
        }
    }

    /**
     * Set bits corresponding to page id
     */
    private void convertIdPageToBits(T callable, char[] bits, int i1, int i2, int i3) {

        if (callable.idPage == 0) {
            if (i1 > -1) bits[i1] = '0';
            bits[i2] = '0';
            bits[i3] = '0';
        } else if (callable.idPage == 1) {
            if (i1 > -1) bits[i1] = '0';
            bits[i2] = '0';
            bits[i3] = '1';
        } else if (callable.idPage == 2) {
            if (i1 > -1) bits[i1] = '0';
            bits[i2] = '1';
            bits[i3] = '0';
        } else if (callable.idPage == 3) {
            if (i1 > -1) bits[i1] = '0';
            bits[i2] = '1';
            bits[i3] = '1';
        } else if (callable.idPage == 4) {
            if (i1 > -1) bits[i1] = '1';
            bits[i2] = '0';
            bits[i3] = '0';
        } else if (callable.idPage == 5) {
            if (i1 > -1) bits[i1] = '1';
            bits[i2] = '0';
            bits[i3] = '1';
        } else if (callable.idPage == 6) {
            if (i1 > -1) bits[i1] = '1';
            bits[i2] = '1';
            bits[i3] = '0';
        } else if (callable.idPage == 7) {
            if (i1 > -1) bits[i1] = '1';
            bits[i2] = '1';
            bits[i3] = '1';
        }
    }

    private String stop(List<char[]> bytes, ExecutorService taskExecutor) {
        
        // Await for termination
        var isTerminated = false;
        
        try {
            taskExecutor.shutdown();
            isTerminated = taskExecutor.awaitTermination(0, TimeUnit.SECONDS);
            
        } catch (InterruptedException e) {
            
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
            Thread.currentThread().interrupt();
        }
        
        if (!isTerminated) {
            
            // awaitTermination timed out, interrupt everything
            taskExecutor.shutdownNow();
        }

        // Get current progress and display
        var result = new StringBuilder();
        
        for (char[] c: bytes) {
            
            try {
                var charCode = Integer.parseInt(new String(c), 2);
                var str = Character.toString((char) charCode);
                result.append(str);
                
            } catch (NumberFormatException err) {
                // Byte string not fully constructed : 0x1x010x
                // Ignore
            }
        }
        
        return result.toString();
    }

    /**
     * Run a HTTP call via the model.
     * @param urlString URL to inject
     * @return Source code
     */
    public String callUrl(String urlString, String metadataInjectionProcess) {
        return this.injectionModel.injectWithoutIndex(urlString, metadataInjectionProcess);
    }

    public String callUrl(String urlString, String metadataInjectionProcess, AbstractCallableBoolean<?> callableBoolean) {
        return this.injectionModel.injectWithoutIndex(urlString, metadataInjectionProcess, callableBoolean);
    }

    public BooleanMode getBooleanMode() {
        return this.booleanMode;
    }
}
