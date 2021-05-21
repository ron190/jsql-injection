package com.jsql.view.swing.console;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.Optional;
import java.util.stream.Stream;

import javax.swing.SwingUtilities;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import com.jsql.util.LogLevel;
import com.jsql.view.swing.util.UiUtil;

/**
 * Log4j2
 * LOGGER.info(e)
 * => No query string
 * LOGGER.info(e.getMessage())
 * => com.jsql.model.exception.InjectionFailureException: No query string
 * LOGGER.info(e, e)
 * => com.jsql.model.exception.InjectionFailureException: No query string + Stacktrace
 */
@Plugin(
    name = "JTextPaneAppender",
    category = Core.CATEGORY_NAME,
    elementType = Appender.ELEMENT_TYPE,
    printObject = true
)
public class JTextPaneAppender extends AbstractAppender {
    
    // Main console
    private static SimpleConsoleAdapter consoleTextPane;
    
    // Java console
    private static JavaConsoleAdapter javaTextPane;

    public static final SimpleAttributeSet ATTRIBUTE_WARN = new SimpleAttributeSet();
    public static final SimpleAttributeSet ATTRIBUTE_INFORM = new SimpleAttributeSet();
    public static final SimpleAttributeSet ATTRIBUTE_SUCCESS = new SimpleAttributeSet();
    public static final SimpleAttributeSet ATTRIBUTE_ALL = new SimpleAttributeSet();
    
    static {
        Stream
        .of(
            new AbstractMap.SimpleEntry<>(ATTRIBUTE_WARN, Color.RED),
            new AbstractMap.SimpleEntry<>(ATTRIBUTE_INFORM, Color.BLUE),
            new AbstractMap.SimpleEntry<>(ATTRIBUTE_SUCCESS, UiUtil.COLOR_GREEN),
            new AbstractMap.SimpleEntry<>(ATTRIBUTE_ALL, Color.BLACK)
        )
        .forEach(entry -> {
            
            StyleConstants.setFontFamily(entry.getKey(), UiUtil.FONT_NAME_MONO_NON_ASIAN);
            StyleConstants.setFontSize(entry.getKey(), UiUtil.FONT_SIZE_MONO_NON_ASIAN);
            StyleConstants.setForeground(entry.getKey(), entry.getValue());
        });
    }
    
    private JTextPaneAppender(String name, Layout<?> layout, Filter filter, boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions, Property.EMPTY_ARRAY);
    }

    @SuppressWarnings({ "unused", "rawtypes" })
    @PluginFactory
    public static JTextPaneAppender createAppender(
        @PluginAttribute("name") String name,
        @PluginAttribute("ignoreExceptions") boolean ignoreExceptions,
        @PluginElement("Layout") Layout layout,
        @PluginElement("Filters") Filter filter
    ) {
        if (name == null) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, "No name provided for JTextPaneAppender");
            return null;
        }

        var layoutTextPane = Optional.ofNullable(layout).orElse(PatternLayout.createDefaultLayout());
        
        return new JTextPaneAppender(name, layoutTextPane, filter, ignoreExceptions);
    }

    @Override
    public void append(LogEvent event) {
        
        // Avoid errors which might occur in headless mode
        // or logging that occurs before consoles are available
        if (consoleTextPane == null || javaTextPane == null) {
            
            return;
        }
        
        var messageLogEvent = new String[] {
            new String(this.getLayout().toByteArray(event), StandardCharsets.UTF_8)
        };
        
        var level = event.getLevel().intLevel();
        
        SwingUtilities.invokeLater(() -> {
            
            String message = messageLogEvent[0];
            
            if (level == LogLevel.CONSOLE_JAVA.intLevel()) {
                
                javaTextPane.append(message, ATTRIBUTE_WARN);
                
            } else if (level == LogLevel.CONSOLE_ERROR.intLevel()) {
                
                consoleTextPane.append(message, ATTRIBUTE_WARN);
                
            } else if (level == LogLevel.CONSOLE_INFORM.intLevel()) {
                
                consoleTextPane.append(message, ATTRIBUTE_INFORM);
                
            } else if (level == LogLevel.CONSOLE_SUCCESS.intLevel()) {
                
                consoleTextPane.append(message, ATTRIBUTE_SUCCESS);
                
            } else if (level == LogLevel.IGNORE.intLevel()) {
                
                // Ignore exception
                
            } else {
                
                consoleTextPane.append(message, ATTRIBUTE_ALL);
            }
        });
    }
    
    /**
     * Register the java console.
     * @param javaConsole
     */
    public static void register(JavaConsoleAdapter javaConsole) {
        
        JTextPaneAppender.javaTextPane = javaConsole;
    }

    /**
     * Register the default console.
     * @param consoleColored
     */
    public static void register(SimpleConsoleAdapter consoleColored) {
        
        JTextPaneAppender.consoleTextPane = consoleColored;
    }
}