package com.jsql.util;

import java.lang.reflect.InvocationTargetException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.swing.SwingUtilities;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.IgnoreMessageException;
import com.jsql.util.bruter.HashUtil;

/**
 * Utility class managing an exception reporting mechanism.
 * It uses Github as the issue webtracker.
 */
public class ExceptionUtil {
    
    /**
     * Using default log4j.properties from root /
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    private InjectionModel injectionModel;
    
    private Set<String> exceptionsMd5Cached = new CopyOnWriteArraySet<>();
    
    public ExceptionUtil(InjectionModel injectionModel) {
        
        this.injectionModel = injectionModel;
    }
    
    /**
     * Handler class processing errors on top of the JVM in order to send
     * a report to Github automatically.
     */
    public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread thread, Throwable throwable) {
            
            // for other uncaught exceptions
            LOGGER.error(
                String.format("Unhandled Exception on %s", thread.getName()),
                throwable
            );

            // Report #214: ignore if OutOfMemoryError: Java heap space
            if (
                ExceptionUtil.this.injectionModel.getMediatorUtils().getPreferencesUtil().isReportingBugs()
                && ExceptionUtils.getStackTrace(throwable).contains("com.jsql")
                && !(throwable instanceof OutOfMemoryError)
            ) {
                
                try {
                    MessageDigest md = MessageDigest.getInstance("Md5");
                    
                    String stackTrace = ExceptionUtils.getStackTrace(throwable).trim();
                    String passwordString = String.valueOf(stackTrace.toCharArray());
                    
                    byte[] passwordByte = passwordString.getBytes();
                    md.update(passwordByte, 0, passwordByte.length);
                    
                    byte[] encodedPassword = md.digest();
                    String encodedPasswordInString = HashUtil.digestToHexString(encodedPassword);
                    
                    String md5Exception = encodedPasswordInString;
                    
                    if (!ExceptionUtil.this.exceptionsMd5Cached.contains(md5Exception)) {
                        
                        ExceptionUtil.this.exceptionsMd5Cached.add(md5Exception);
                        ExceptionUtil.this.injectionModel.getMediatorUtils().getGitUtil()
                        .sendUnhandledException(
                            thread.getName(),
                            throwable
                        );
                    }
                    
                } catch (NoSuchAlgorithmException e) {
                    
                    // Ignore
                    IgnoreMessageException exceptionIgnored = new IgnoreMessageException(e);
                    LOGGER.trace(exceptionIgnored, exceptionIgnored);
                }
            }
        }
    }
    
    /**
     * Add the error reporting mechanism on top of the JVM in order to
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
            Thread.currentThread().interrupt();
        }
    }
}
