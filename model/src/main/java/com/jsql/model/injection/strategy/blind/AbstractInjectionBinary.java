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
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractInjectionBinary<T extends AbstractCallableBinary<T>> {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    // Every FALSE SQL statements will be checked,
    // more statements means a more robust application
    protected final List<String> falsy;
    
    // Every TRUE SQL statements will be checked,
    // more statements means a more robust application
    protected final List<String> truthy;
    
    public enum BinaryMode {
        AND, OR, STACK, NO_MODE
    }
    
    protected final InjectionModel injectionModel;
    
    protected final BinaryMode binaryMode;
    
    protected AbstractInjectionBinary(InjectionModel injectionModel, BinaryMode binaryMode) {
        this.injectionModel = injectionModel;
        this.binaryMode = binaryMode;
        this.falsy = this.injectionModel.getMediatorVendor().getVendor().instance().getFalsy();
        this.truthy = this.injectionModel.getMediatorVendor().getVendor().instance().getTruthy();
    }

    /**
     * Start one test to verify if boolean works.
     * @return true if boolean method is confirmed
     */
    public abstract boolean isInjectable() throws StoppedByUserSlidingException;

    public abstract void initNextChars(
        String sqlQuery,
        List<char[]> bytes,
        AtomicInteger indexCharacter,
        CompletionService<T> taskCompletionService,
        AtomicInteger countTasksSubmitted
    );

    public abstract char[] initBinaryMask(List<char[]> bytes, T currentCallable);

    /**
     * Display a message to explain how is blind/time working.
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
        // e.g. SQLi: bytes[0] => 01010011:S, bytes[1] => 01010001:Q ...
        List<char[]> bytes = new ArrayList<>();
        
        // Cursor for current character position
        var indexCharacter = new AtomicInteger(0);

        // Concurrent URL requests
        ExecutorService taskExecutor = this.injectionModel.getMediatorUtils().getThreadUtil().getExecutor("CallableAbstractBoolean");
        
        CompletionService<T> taskCompletionService = new ExecutorCompletionService<>(taskExecutor);

        var countTasksSubmitted = new AtomicInteger(0);
        var countBadAsciiCode = new AtomicInteger(0);

        this.initNextChars(sqlQuery, bytes, indexCharacter, taskCompletionService, countTasksSubmitted);

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
                this.initNextChars(sqlQuery, bytes, indexCharacter, taskCompletionService, countTasksSubmitted);

                String result = AbstractInjectionBinary.convert(bytes);
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
        
        char[] asciiCodeMask = this.initBinaryMask(bytes, currentCallable);
        var asciiCodeBinary = new String(asciiCodeMask);

        // Inform the View if bits array is complete, else nothing #Need fix
        if (asciiCodeBinary.matches("^[01]{8}$")) {
            var asciiCode = Integer.parseInt(asciiCodeBinary, 2);
            if (asciiCode == 127 || asciiCode == 0) {  // Stop if many 11111111, 01111111 or 00000000
                if (countTasksSubmitted.get() != 0 && countBadAsciiCode.get() > 15) {
                    throw new InjectionFailureException("Boolean false positive, stopping...");
                }
                countBadAsciiCode.incrementAndGet();
            }

            currentCallable.charText = Character.toString((char) asciiCode);
            
            var interaction = new Request();
            interaction.setMessage(Interaction.MESSAGE_BINARY);
            interaction.setParameters(
                asciiCodeBinary
                + "="
                + currentCallable.charText
                .replace("\\n", "\\\\\\n")
                .replace("\\r", "\\\\\\r")
                .replace("\\t", "\\\\\\t")
            );
            this.injectionModel.sendToViews(interaction);
        }
    }

    private String stop(List<char[]> bytes, ExecutorService taskExecutor) {
        this.injectionModel.getMediatorUtils().getThreadUtil().shutdown(taskExecutor);

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

    public String callUrl(String urlString, String metadataInjectionProcess, AbstractCallableBinary<?> callableBoolean) {
        return this.injectionModel.injectWithoutIndex(urlString, metadataInjectionProcess, callableBoolean);
    }

    public BinaryMode getBooleanMode() {
        return this.binaryMode;
    }
}
