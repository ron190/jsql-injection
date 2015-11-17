package com.jsql.view.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.StringWriter;

import javax.swing.SwingUtilities;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

import com.jsql.view.swing.console.JColoredConsole;
import com.jsql.view.swing.console.JavaConsoleAdapter;
import com.jsql.view.swing.console.SimpleConsoleAdapter;

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
    private static JavaConsoleAdapter javaConsole;

    private static final SimpleAttributeSet FATAL = new SimpleAttributeSet();
    private static final SimpleAttributeSet ERROR = new SimpleAttributeSet();
    private static final SimpleAttributeSet WARN = new SimpleAttributeSet();
    private static final SimpleAttributeSet INFO = new SimpleAttributeSet();
    private static final SimpleAttributeSet DEBUG = new SimpleAttributeSet();
    private static final SimpleAttributeSet TRACE = new SimpleAttributeSet();
    private static final SimpleAttributeSet ALL = new SimpleAttributeSet();

    static {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            try {
                ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new BufferedInputStream(SwingAppender.class.getResourceAsStream("/com/jsql/UbuntuMono-R.ttf"))));
            } catch (FontFormatException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            StyleConstants.setFontFamily(FATAL, "Ubuntu Mono");
            StyleConstants.setFontFamily(ERROR, "Ubuntu Mono");
            StyleConstants.setFontFamily(WARN, "Ubuntu Mono");
            StyleConstants.setFontFamily(INFO, "Ubuntu Mono");
            StyleConstants.setFontFamily(DEBUG, "Ubuntu Mono");
            StyleConstants.setFontFamily(TRACE, "Ubuntu Mono");
            StyleConstants.setFontFamily(ALL, "Ubuntu Mono");
            
            StyleConstants.setFontSize(FATAL, 14);
            StyleConstants.setFontSize(ERROR, 14);
            StyleConstants.setFontSize(WARN, 14);
            StyleConstants.setFontSize(INFO, 14);
            StyleConstants.setFontSize(DEBUG, 14);
            StyleConstants.setFontSize(TRACE, 14);
            StyleConstants.setFontSize(ALL, 14);
            
            StyleConstants.setForeground(FATAL, Color.blue);
            StyleConstants.setForeground(ERROR, Color.red);
            StyleConstants.setForeground(WARN, Color.red);
            StyleConstants.setForeground(INFO, Color.blue);
            StyleConstants.setForeground(DEBUG, new Color(0, 128, 0));
            StyleConstants.setForeground(TRACE, Color.black);
            StyleConstants.setForeground(ALL, Color.black);
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
            case Level.TRACE_INT:
                consoleColored.append(message, TRACE);
                consoleColored.getProxy().setCaretPosition(consoleColored.getProxy().getDocument().getLength());
                break;
            case Level.ALL_INT:
                consoleColored.append(message, ALL);
                consoleColored.getProxy().setCaretPosition(consoleColored.getProxy().getDocument().getLength());
                break;
            case Level.FATAL_INT:
                consoleColored.append(message, FATAL);
                consoleColored.getProxy().setCaretPosition(consoleColored.getProxy().getDocument().getLength());
                break;
            case Level.ERROR_INT:
                javaConsole.append(message, WARN);
                javaConsole.getProxy().setCaretPosition(javaConsole.getProxy().getDocument().getLength());
                
                if (throwableInformation != null && throwableInformation.getThrowableStrRep() != null) {
                    for (String rep: throwableInformation.getThrowableStrRep()) {
                        javaConsole.append(rep, ERROR);
                    }
                }
                break;
            case Level.WARN_INT:
                consoleColored.append(message, WARN);
                consoleColored.getProxy().setCaretPosition(consoleColored.getProxy().getDocument().getLength());
                break;
            case Level.INFO_INT:
                consoleColored.append(message, INFO);
                consoleColored.getProxy().setCaretPosition(consoleColored.getProxy().getDocument().getLength());
                break;
            case Level.DEBUG_INT:
                consoleColored.append(message, DEBUG);
                consoleColored.getProxy().setCaretPosition(consoleColored.getProxy().getDocument().getLength());
                break;
            default:
                consoleColored.append(message, ALL);
                consoleColored.getProxy().setCaretPosition(consoleColored.getProxy().getDocument().getLength());
                break;
        }
    }

    /**
     * Register the java console.
     * @param javaConsole
     */
    public static void register(JavaConsoleAdapter javaConsole) {
        SwingAppender.javaConsole = javaConsole;
    }

    /**
     * Register the default console.
     * @param consoleColored
     */
    public static void register(SimpleConsoleAdapter consoleColored) {
        SwingAppender.consoleColored = consoleColored;
    }
}
