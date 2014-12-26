package com.jsql;

import org.apache.log4j.Logger;

import com.jsql.model.injection.InjectionModel;
import com.jsql.model.injection.MediatorModel;
import com.jsql.view.GUI;
import com.jsql.view.MediatorGUI;

public class Application {
    /**
     * Application starting point.
     * @param args CLI parameters (not used)
     */
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
        System.setProperty("sun.awt.exception.handler", ExceptionHandler.class.getName());
        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                InjectionModel model = new InjectionModel();
                MediatorModel.register(model);
                MediatorGUI.register(model);
                MediatorGUI.register(new GUI());
            }
        });
    }
    
    public static final Logger LOGGER = Logger.getLogger(Application.class);

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
        }
    }
}
