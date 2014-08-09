package com.jsql.view;

import java.awt.Color;

import javax.swing.SwingUtilities;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

import com.jsql.view.console.AdapterDefaultColoredConsole;
import com.jsql.view.console.AdapterJavaConsole;
import com.jsql.view.console.JColoredConsole;

/**
 * Log4j swing appender to display console message to respective textareas.
 */
public class SwingAppender extends WriterAppender {
    /**
     * Main console textfield.
     */
    private static JColoredConsole consoleColored;
    
    /**
     * Java console textfield.
     */
    private AdapterJavaConsole javaConsole;

    private static final SimpleAttributeSet ERROR = new SimpleAttributeSet();
    private static final SimpleAttributeSet INFO = new SimpleAttributeSet();
    private static final SimpleAttributeSet ALL = new SimpleAttributeSet();
    private static final SimpleAttributeSet FATAL = new SimpleAttributeSet();
    private static final SimpleAttributeSet WARN = new SimpleAttributeSet();
    private static final SimpleAttributeSet DEBUG = new SimpleAttributeSet();

    // Best to reuse attribute sets as much as possible.
    static {
        StyleConstants.setForeground(ALL, Color.green);
        StyleConstants.setForeground(FATAL, Color.red);
        //        StyleConstants.setItalic(ERROR, true);

        StyleConstants.setForeground(ERROR, Color.red);
        //        StyleConstants.setBold(ERROR, true);

//        StyleConstants.setForeground(WARN, Color.yellow);
        StyleConstants.setForeground(WARN, Color.red);

        //        StyleConstants.setForeground(INFO, Color.blue);
        StyleConstants.setForeground(DEBUG, Color.green);
        StyleConstants.setItalic(DEBUG, true);
    }

    public SwingAppender() {
        this.setLayout(new PatternLayout("[%-5p] (%F:%L) - %m%n"));
    }

    /**
     * Method from Log4j AppenderSkeleton that gets a call for all Log4J events.
     *
     * @param event A logging event.
     * @see org.apache.log4j.AppenderSkeleton
     */
    public void append(final LoggingEvent event) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                insertText(layout.format(event), event.getLevel(), event
                        .getThrowableInformation());
            }
        });
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

        switch (level.toInt()) {
            case Level.ALL_INT:
                break;
            case Level.FATAL_INT:
                break;
            case Level.ERROR_INT:
                for (String rep: throwableInformation.getThrowableStrRep()) {
                    javaConsole.append(rep, ERROR);
                }
                break;
            case Level.WARN_INT:
                consoleColored.append(message, WARN);
                consoleColored.setCaretPosition(consoleColored.getDocument().getLength());
                break;
            case Level.INFO_INT:
                consoleColored.append(message, INFO);
                consoleColored.setCaretPosition(consoleColored.getDocument().getLength());
                break;
            case Level.DEBUG_INT:
                break;
            default:
                break;
        }
    }

    /**
     * Register the java console.
     * @param javaConsole
     */
    public void register(AdapterJavaConsole javaConsole) {
        this.javaConsole = javaConsole;
    }

    /**
     * Register the default console.
     * @param consoleColored
     */
    public void register(AdapterDefaultColoredConsole consoleColored) {
        SwingAppender.consoleColored = consoleColored;
    }
}
