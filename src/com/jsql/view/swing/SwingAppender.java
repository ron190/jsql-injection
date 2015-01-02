package com.jsql.view.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;

import javax.swing.SwingUtilities;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

import com.jsql.tool.ToolsString;
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

    private static final SimpleAttributeSet ERROR = new SimpleAttributeSet();
    private static final SimpleAttributeSet INFO = new SimpleAttributeSet();
//    private static final SimpleAttributeSet ALL = new SimpleAttributeSet();
//    private static final SimpleAttributeSet FATAL = new SimpleAttributeSet();
    private static final SimpleAttributeSet WARN = new SimpleAttributeSet();
//    private static final SimpleAttributeSet DEBUG = new SimpleAttributeSet();

    // Best to reuse attribute sets as much as possible.
    static {
//        StyleConstants.setForeground(ALL, Color.green);
//        StyleConstants.setForeground(FATAL, Color.red);
        //        StyleConstants.setItalic(ERROR, true);
        GraphicsEnvironment ge = 
                GraphicsEnvironment.getLocalGraphicsEnvironment();
            try {
//                ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("Untitled1.ttf")));
//                ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("/src/UbuntuMono-Regular.ttf")));
//                ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new BufferedInputStream(SwingAppender.class.getResourceAsStream("/jsql-font.ttf"))));
                ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new BufferedInputStream(SwingAppender.class.getResourceAsStream("/UbuntuMono-R.ttf"))));
            } catch (FontFormatException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            StyleConstants.setFontFamily(INFO, "Ubuntu Mono");
            StyleConstants.setFontFamily(ERROR, "Ubuntu Mono");
            StyleConstants.setFontSize(INFO, 14);
            StyleConstants.setFontSize(ERROR, 14);

//        StyleConstants.setFontFamily(INFO, "jsql font");
        StyleConstants.setForeground(ERROR, Color.red);
//        StyleConstants.setFontSize(INFO, 24);
        //        StyleConstants.setBold(ERROR, true);

//        StyleConstants.setForeground(WARN, Color.yellow);
        StyleConstants.setForeground(WARN, Color.red);

        //        StyleConstants.setForeground(INFO, Color.blue);
//        StyleConstants.setForeground(DEBUG, Color.green);
//        StyleConstants.setItalic(DEBUG, true);
    }

    public SwingAppender() {
        this.setLayout(new PatternLayout("[%-5p] (%F:%L) - %m%n"));
        this.setWriter(new StringWriter());
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
                javaConsole.append(message, WARN);
                javaConsole.setCaretPosition(javaConsole.getDocument().getLength());
                
                if (throwableInformation.getThrowableStrRep() != null) {
                    for (String rep: throwableInformation.getThrowableStrRep()) {
                        javaConsole.append(rep, ERROR);
                    }
                }
                break;
            case Level.WARN_INT:
                consoleColored.append(message,WARN);
                consoleColored.setCaretPosition(consoleColored.getDocument().getLength());
                break;
            case Level.INFO_INT:
                consoleColored.append(message,INFO);
//                consoleColored.appendHex(ToolsString.hexstr("000102030405"), INFO);
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
    public static void register(JavaConsoleAdapter javaConsole) {
        SwingAppender.javaConsole = javaConsole;
    }

    /**
     * Register the default console.
     * @param consoleColored
     */
//    public void register(SimpleConsoleAdapter consoleColored) {
    public static void register(SimpleConsoleAdapter consoleColored) {
        SwingAppender.consoleColored = consoleColored;
    }
}
