package com.jsql.model.accessible;

import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.IgnoreMessageException;
import com.jsql.model.exception.InjectionFailureException;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.suspendable.SuspendableGetRows;

/**
 * Thread unit to read source of a file by SQL injection.
 * User can interrupt the process and get a partial result of the file content.
 */
public class CallableFile implements Callable<CallableFile> {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();
	
    /**
     * Path to the file to read.
     */
    private String pathFile;

    /**
     * Source of file.
     */
    private String sourceFile = "";
    
    /**
     * Suspendable task that reads lines of the file by injection.
     */
    private SuspendableGetRows suspendableReadFile;

	/**
     * Create Callable to read a file.
     * @param pathFile
     */
    public CallableFile(String pathFile, InjectionModel injectionModel) {
        this.pathFile = pathFile;
        this.injectionModel= injectionModel;
        this.suspendableReadFile = new SuspendableGetRows(injectionModel);
    }
    InjectionModel injectionModel;
    
    /**
     * Read a file on the server using SQL injection.
     * Get partial result if user interrupts the process.
     */
    @Override
    public CallableFile call() throws Exception {
        String[] sourcePage = {""};

        String resultToParse = "";
        try {
            resultToParse = this.suspendableReadFile.run(
                injectionModel.getVendor().instance().sqlFileRead(this.pathFile),
                sourcePage,
                false,
                1,
                null
            );
        } catch (InjectionFailureException e) {
            // Usually thrown if File does not exist
            
            // Ignore
            IgnoreMessageException exceptionIgnored = new IgnoreMessageException(e);
            LOGGER.trace(exceptionIgnored, exceptionIgnored);
        } catch (StoppedByUserSlidingException e) {
            // Get partial source
            if (!"".equals(e.getSlidingWindowAllRows())) {
                resultToParse = e.getSlidingWindowAllRows();
            } else if (!"".equals(e.getSlidingWindowCurrentRows())) {
                resultToParse = e.getSlidingWindowCurrentRows();
            }
            
            // Ignore
            IgnoreMessageException exceptionIgnored = new IgnoreMessageException(e);
            LOGGER.trace(exceptionIgnored, exceptionIgnored);
        }
        this.sourceFile = resultToParse;
        
        return this;
    }
    
    // Getters and setters
    
    public String getPathFile() {
        return this.pathFile;
    }

    public String getSourceFile() {
    	// TODO optional
        return this.sourceFile;
    }

    public SuspendableGetRows getSuspendableReadFile() {
		return this.suspendableReadFile;
	}
    
}