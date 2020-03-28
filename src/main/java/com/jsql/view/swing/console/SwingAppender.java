package com.jsql.view.swing.console;

import java.awt.Color;
import java.io.StringWriter;
import java.util.AbstractMap;
import java.util.stream.Stream;

import javax.swing.SwingUtilities;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

import com.jsql.model.exception.IgnoreMessageException;
import com.jsql.view.swing.HelperUi;

/**
 * Log4j swing appender to display console message to respective textareas.
 */
public class SwingAppender extends WriterAppender {
    
    /**
     * Main console textfield.
     */
    private static SimpleConsoleAdapter consoleTextPane;
    
    /**
     * Java console textfield.
     */
    private static JavaConsoleAdapter javaTextPane;

    public static final SimpleAttributeSet ERROR = new SimpleAttributeSet();
    public static final SimpleAttributeSet WARN = new SimpleAttributeSet();
    public static final SimpleAttributeSet INFO = new SimpleAttributeSet();
    public static final SimpleAttributeSet DEBUG = new SimpleAttributeSet();
    public static final SimpleAttributeSet TRACE = new SimpleAttributeSet();
    public static final SimpleAttributeSet ALL = new SimpleAttributeSet();
    
    static {
        Stream.of(
            new AbstractMap.SimpleEntry<>(ERROR, Color.RED),
            new AbstractMap.SimpleEntry<>(WARN, Color.RED),
            new AbstractMap.SimpleEntry<>(INFO, Color.BLUE),
            new AbstractMap.SimpleEntry<>(DEBUG, HelperUi.COLOR_GREEN),
            new AbstractMap.SimpleEntry<>(TRACE, Color.BLACK),
            new AbstractMap.SimpleEntry<>(ALL, Color.BLACK)
        ).forEach(entry -> {
            
            StyleConstants.setFontFamily(entry.getKey(), HelperUi.FONT_NAME_UBUNTU_MONO);
            StyleConstants.setFontSize(entry.getKey(), 14);
            StyleConstants.setForeground(entry.getKey(), entry.getValue());
        });
    }

    public SwingAppender() {
        
        this.setLayout(new PatternLayout("[%-5p] (%F:%L) - %m%n"));
        this.setWriter(new StringWriter());
    }

    /**
     * Method from Log4j AppenderSkeleton that gets a call for all Log4J events.
     * @param event A logging event.
     * @see org.apache.log4j.AppenderSkeleton
     */
    @Override
    public void append(final LoggingEvent event) {
        
        SwingUtilities.invokeLater(() -> this.insertText(
            this.layout.format(event),
            event.getLevel(),
            event.getThrowableInformation()
        ));
    }

    /**
     * Requires a layout.
     * @return true.
     */
    @Override
    public boolean requiresLayout() {
        return true;
    }

    /**
     * This method overrides the parent {@link WriterAppender#closeWriter} implementation
     * to do nothing because the console stream is not ours to close.
     */
    @Override
    protected final void closeWriter() {
        // Do nothing
    }

    /**
     * Colorizes the specified message for the specified log4j level.
     */
    private void insertText(String message, Level level, ThrowableInformation throwableInformation) {
        
        // Avoid errors which might occur in headless mode
        if (consoleTextPane == null || javaTextPane == null) {
            return;
        }
        
        switch (level.toInt()) {
            case Level.TRACE_INT:
                if (throwableInformation == null || !(throwableInformation.getThrowable() instanceof IgnoreMessageException)) {
                    consoleTextPane.append(message, TRACE);
                }
                break;
                
            case Priority.ERROR_INT:
                javaTextPane.append(message, WARN);
                javaTextPane.getProxy().setCaretPosition(javaTextPane.getProxy().getDocument().getLength());
                
                if (throwableInformation != null && throwableInformation.getThrowableStrRep() != null) {
                    for (String rep: throwableInformation.getThrowableStrRep()) {
                        javaTextPane.append(rep, ERROR);
                    }
                }
                break;
                
            case Priority.WARN_INT:
                consoleTextPane.append(message, WARN);
                break;
                
            case Priority.INFO_INT:
                consoleTextPane.append(message, INFO);
                break;
                
            case Priority.DEBUG_INT:
                consoleTextPane.append(message, DEBUG);
                break;
                
            case Priority.FATAL_INT:
                // Ignore exception
                break;
                
            case Priority.ALL_INT:
            default:
                consoleTextPane.append(message, ALL);
                break;
        }
    }

    /**
     * Register the java console.
     * @param javaConsole
     */
    public static void register(JavaConsoleAdapter javaConsole) {
        SwingAppender.javaTextPane = javaConsole;
    }

    /**
     * Register the default console.
     * @param consoleColored
     */
    public static void register(SimpleConsoleAdapter consoleColored) {
        SwingAppender.consoleTextPane = consoleColored;
    }
}
