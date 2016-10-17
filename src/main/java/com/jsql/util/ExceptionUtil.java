package com.jsql.util;

import org.apache.log4j.Logger;

public class ExceptionUtil {
    /**
     * Using default log4j.properties from root /
     */
    private static final Logger LOGGER = Logger.getLogger(Exception.class);
    
    /**
     * Utility class.
     */
    private ExceptionUtil() {
        //not called
    }

    public static class ExceptionHandler implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread thread, Throwable thrown) {
            // for other uncaught exceptions
            this.handleException(thread.getName(), thrown);
        }
        
        protected void handleException(String threadName, Throwable throwable) {
            LOGGER.error("Unhandled Exception on "+ threadName, throwable);
            
            //  Report #214: ignore if OutOfMemoryError: Java heap space
            if (
                PreferencesUtil.isReportingBugs() && !(throwable instanceof OutOfMemoryError)
            ) {
                GitUtil.sendUnhandledException(threadName, throwable);
            }
        }
    }
    
    public static void setUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
        System.setProperty("sun.awt.exception.handler", ExceptionHandler.class.getName());
    }
}
