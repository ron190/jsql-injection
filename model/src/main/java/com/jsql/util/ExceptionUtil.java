package com.jsql.util;

import com.jsql.model.InjectionModel;
import com.jsql.util.bruter.HashUtil;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Utility class managing an exception reporting mechanism.
 * It uses Github as the issue webtracker.
 */
public class ExceptionUtil {
    
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    private final InjectionModel injectionModel;
    
    private final Set<String> exceptionsMd5Cached = new CopyOnWriteArraySet<>();
    
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
            
            LOGGER.log(
                LogLevelUtil.CONSOLE_JAVA,
                () -> String.format("Unhandled Exception on %s", thread.getName()),
                throwable
            );
            // Display to stdout
            LOGGER.log(
                Level.ERROR,
                () -> String.format("Unhandled Exception on %s", thread.getName()),
                throwable
            );

            // Report #214: ignore if OutOfMemoryError: Java heap space
            if (
                ExceptionUtil.this.injectionModel.getMediatorUtils().getPreferencesUtil().isReportingBugs()
                && ExceptionUtils.getStackTrace(throwable).contains("com.jsql")
                && !(throwable instanceof OutOfMemoryError)
            ) {

                if (ExceptionUtils.getStackTrace(throwable).contains("Could not initialize class java.awt.Toolkit")) {

                    LOGGER.log(LogLevelUtil.CONSOLE_JAVA, "System libraries are missing, please use a proper Java runtime instead of headless runtime");
                    return;

                } else if (ExceptionUtils.getStackTrace(throwable).contains("Could not initialize class sun.awt.X11.XToolkit")) {

                    LOGGER.log(LogLevelUtil.CONSOLE_JAVA, "System libraries are missing or wrong DISPLAY variable, please verify your settings");
                    return;
                }

                try {
                    var md = MessageDigest.getInstance("Md5");

                    String stackTrace = ExceptionUtils.getStackTrace(throwable).trim();
                    var passwordString = String.valueOf(stackTrace.toCharArray());

                    byte[] passwordByte = passwordString.getBytes();
                    md.update(passwordByte, 0, passwordByte.length);

                    byte[] encodedPassword = md.digest();

                    var md5Exception = HashUtil.digestToHexString(encodedPassword);

                    if (!ExceptionUtil.this.exceptionsMd5Cached.contains(md5Exception)) {

                        ExceptionUtil.this.exceptionsMd5Cached.add(md5Exception);
                        ExceptionUtil.this.injectionModel.getMediatorUtils().getGitUtil().sendUnhandledException(
                            thread.getName(),
                            throwable
                        );
                    }

                } catch (NoSuchAlgorithmException e) {

                    LOGGER.log(LogLevelUtil.IGNORE, e);
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
            
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
            Thread.currentThread().interrupt();
        }
    }
}
