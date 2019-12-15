package com.jsql.util;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import com.jsql.model.InjectionModel;

/**
 * Utility class managing an exception reporting mecanism.
 * It uses Github as the issue webtracker.
 */
public class ExceptionUtil {
	
    /**
     * Using default log4j.properties from root /
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
    // Utility class
    private ExceptionUtil() {
        // not called
    }

    public ExceptionUtil(InjectionModel injectionModel) {
        this.injectionModel = injectionModel;
    }
    InjectionModel injectionModel;

    /**
     * Handler class processing errors on top of the JVM in order to send
     * a report to Github automatically.
     */
    public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread thread, Throwable throwable) {
            // for other uncaught exceptions
            LOGGER.error("Unhandled Exception on "+ thread.getName(), throwable);
            
            //  Report #214: ignore if OutOfMemoryError: Java heap space
            if (
                ExceptionUtil.this.injectionModel.preferencesUtil.isReportingBugs()
                && ExceptionUtils.getStackTrace(throwable).contains("com.jsql")
                && !(throwable instanceof OutOfMemoryError)
            ) {
                ExceptionUtil.this.injectionModel.gitUtil.sendUnhandledException(thread.getName(), throwable);
            }
        }
        
    }
    
    /**
     * Add the error reporting mecanism on top of the JVM in order to
     * intercept and process the error to Github.
     */
    public void setUncaughtExceptionHandler() {
    	
    	// Regular Exception
    	Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());

    	// Event dispatching thread Exception
    	try {
			SwingUtilities.invokeAndWait(() ->
		        // We are in the event dispatching thread
				Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler())
		    );
		} catch (InvocationTargetException | InterruptedException e) {
			LOGGER.error("Unhandled Exception on ExceptionUtil", e);
		}
    	
    }
    
}
