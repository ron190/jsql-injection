package com.jsql.model.accessible;

import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.InjectionFailureException;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.suspendable.SuspendableGetRows;
import com.jsql.util.LogLevel;

/**
 * Thread unit to read source of a file by SQL injection.
 * User can interrupt the process and get a partial result of the file content.
 */
public class CallableFile implements Callable<CallableFile> {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    /**
     * Path to the file to read.
     */
    private String pathFile;

    /**
     * Source of file.
     */
    private String sourceFile = StringUtils.EMPTY;
    
    /**
     * Suspendable task that reads lines of the file by injection.
     */
    private SuspendableGetRows suspendableReadFile;

    private InjectionModel injectionModel;
    
    /**
     * Create Callable to read a file.
     * @param pathFile
     */
    public CallableFile(String pathFile, InjectionModel injectionModel) {
        
        this.pathFile = pathFile;
        this.injectionModel= injectionModel;
        this.suspendableReadFile = new SuspendableGetRows(injectionModel);
    }
    
    /**
     * Read a file on the server using SQL injection.
     * Get partial result if user interrupts the process.
     */
    @Override
    public CallableFile call() throws Exception {
        
        var sourcePage = new String[]{ StringUtils.EMPTY };

        String resultToParse = StringUtils.EMPTY;
        try {
            resultToParse = this.suspendableReadFile.run(
                this.injectionModel.getMediatorVendor().getVendor().instance().sqlFileRead(this.pathFile),
                sourcePage,
                false,
                1,
                null,
                "file"
            );
            
        } catch (InjectionFailureException e) {
            
            // Usually thrown if File does not exist
            
            LOGGER.log(LogLevel.IGNORE, e);
            
        } catch (StoppedByUserSlidingException e) {
            
            // Get partial source
            if (StringUtils.isNotEmpty(e.getSlidingWindowAllRows())) {
                
                resultToParse = e.getSlidingWindowAllRows();
                
            } else if (StringUtils.isNotEmpty(e.getSlidingWindowCurrentRows())) {
                
                resultToParse = e.getSlidingWindowCurrentRows();
            }
            
            LOGGER.log(LogLevel.IGNORE, e);
        }
        
        this.sourceFile = resultToParse;
        
        return this;
    }
    
    
    // Getters and setters
    
    public String getPathFile() {
        return this.pathFile;
    }

    public String getSourceFile() {
        return this.sourceFile;
    }

    public SuspendableGetRows getSuspendableReadFile() {
        return this.suspendableReadFile;
    }
}