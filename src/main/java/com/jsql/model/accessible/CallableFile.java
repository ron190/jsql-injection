package com.jsql.model.accessible;

import java.util.concurrent.Callable;

import com.jsql.model.MediatorModel;
import com.jsql.model.exception.InjectionFailureException;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.suspendable.SuspendableGetRows;

/**
 * Thread unit to read source of a file by SQL injection.
 * User can interrupt the process and get a partial result of the file content.
 */
public class CallableFile implements Callable<CallableFile> {
	
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
    private SuspendableGetRows suspendableReadFile = new SuspendableGetRows();

	/**
     * Create Callable to read a file.
     * @param pathFile
     */
    public CallableFile(String pathFile) {
        this.pathFile = pathFile;
    }
    
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
                MediatorModel.model().vendor.instance().sqlFileRead(this.pathFile),
                sourcePage,
                false,
                1,
                null
            );
        } catch (InjectionFailureException e) {
            // Ignore
            // Usually thrown if File does not exist
        } catch (StoppedByUserSlidingException e) {
            // Get partial source
            if (!"".equals(e.getSlidingWindowAllRows())) {
                resultToParse = e.getSlidingWindowAllRows();
            } else if (!"".equals(e.getSlidingWindowCurrentRows())) {
                resultToParse = e.getSlidingWindowCurrentRows();
            }
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