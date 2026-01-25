package com.jsql.util;

import com.jsql.model.InjectionModel;
import com.jsql.util.bruter.Coder;
import com.jsql.util.bruter.HashUtil;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Utility class managing exception reporting to GitHub.
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
     * a report to GitHub automatically.
     */
    public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread thread, Throwable throwable) {
            LOGGER.log(
                LogLevelUtil.CONSOLE_JAVA,
                () -> String.format("Unhandled Exception on %s", thread.getName()),
                throwable
            );
            LOGGER.log(  // Display to stdout
                Level.ERROR,
                () -> String.format("Unhandled Exception on %s", thread.getName()),
                throwable
            );

            // Report #214: ignore if OutOfMemoryError: Java heap space
            if (
                ExceptionUtil.this.injectionModel.getMediatorUtils().preferencesUtil().isReportingBugs()
                && ExceptionUtils.getStackTrace(throwable).contains("com.jsql")
                && !(throwable instanceof OutOfMemoryError)
                && !ExceptionUtils.getStackTrace(throwable).contains("OutOfMemoryError")  // when implicit
            ) {
                if (ExceptionUtils.getStackTrace(throwable).contains("Could not initialize class java.awt.Toolkit")) {
                    LOGGER.log(LogLevelUtil.CONSOLE_JAVA, "System libraries are missing, please use a proper Java runtime instead of headless runtime");
                    return;
                } else if (ExceptionUtils.getStackTrace(throwable).contains("Could not initialize class sun.awt.X11.XToolkit")) {
                    LOGGER.log(LogLevelUtil.CONSOLE_JAVA, "System libraries are missing or wrong DISPLAY variable, please verify your settings");
                    return;
                }

                try {
                    var messageDigest = MessageDigest.getInstance(Coder.MD5.label);

                    String stackTrace = ExceptionUtils.getStackTrace(throwable).trim();
                    var passwordString = String.valueOf(stackTrace.toCharArray());

                    byte[] passwordByte = passwordString.getBytes(StandardCharsets.UTF_8);
                    messageDigest.update(passwordByte, 0, passwordByte.length);

                    byte[] encodedPassword = messageDigest.digest();
                    var md5Exception = HashUtil.digestToHexString(encodedPassword);
                    if (!ExceptionUtil.this.exceptionsMd5Cached.contains(md5Exception)) {
                        ExceptionUtil.this.exceptionsMd5Cached.add(md5Exception);
                        ExceptionUtil.this.injectionModel.getMediatorUtils().gitUtil().sendUnhandledException(
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
     * intercept and process the error to GitHub.
     */
    public void setUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());  // Regular Exception
        try {  // Event dispatching thread Exception
            SwingUtilities.invokeAndWait(() ->
                Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler())  // We are in the event dispatching thread
            );
        } catch (InvocationTargetException | InterruptedException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
            Thread.currentThread().interrupt();
        }
    }
}
