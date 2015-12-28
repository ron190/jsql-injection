package com.jsql.tool;

import org.apache.log4j.Logger;

import com.jsql.model.injection.MediatorModel;

public class ExceptionTools {
    /**
     * Using default log4j.properties from root /
     */
    public static final Logger LOGGER = Logger.getLogger(ExceptionTools.class);
    
    /**
     * Utility class.
     */
    private ExceptionTools() {
        //not called
    }

    public static class ExceptionHandler implements Thread.UncaughtExceptionHandler {
        
        public void handle(Throwable thrown) {
            // for EDT exceptions
            handleException(Thread.currentThread().getName(), thrown);
        }
        
        public void uncaughtException(Thread thread, Throwable thrown) {
            // for other uncaught exceptions
            handleException(thread.getName(), thrown);
        }
        
        protected void handleException(String tname, Throwable thrown) {
            LOGGER.error("Exception on " + tname, thrown);
            
            //  Report #214: ignore if OutOfMemoryError: Java heap space 
            if (MediatorModel.model().reportBugs && !"OutOfMemoryError".equals(thrown.getClass().getSimpleName())) {
                GitTools.sendUnhandledException(tname, thrown);
            }
        }
    }
    
    public static void setUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
        System.setProperty("sun.awt.exception.handler", ExceptionHandler.class.getName());
    }
}
