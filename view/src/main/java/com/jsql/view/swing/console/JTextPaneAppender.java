package com.jsql.view.swing.console;

import com.jsql.util.LogLevelUtil;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Plugin(
    name = "JTextPaneAppender",
    category = Core.CATEGORY_NAME,
    elementType = Appender.ELEMENT_TYPE,
    printObject = true
)
public class JTextPaneAppender extends AbstractAppender {
    
    private static SimpleConsoleAdapter consoleTextPane;  // Main console
    private static SimpleConsoleAdapter javaTextPane;  // Java console

    public static final SimpleAttributeSet ATTRIBUTE_WARN = new SimpleAttributeSet();
    public static final SimpleAttributeSet ATTRIBUTE_INFORM = new SimpleAttributeSet();
    public static final SimpleAttributeSet ATTRIBUTE_SUCCESS = new SimpleAttributeSet();
    public static final SimpleAttributeSet ATTRIBUTE_ALL = new SimpleAttributeSet();
    
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
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, "No name provided for JTextPaneAppender");
            return null;
        }
        var layoutTextPane = Optional.ofNullable(layout).orElse(PatternLayout.createDefaultLayout());
        return new JTextPaneAppender(name, layoutTextPane, filter, ignoreExceptions);
    }

    @Override
    public void append(LogEvent event) {
        // Avoid errors which might occur in headless mode
        // or logging that occurs before consoles are available
        if (JTextPaneAppender.consoleTextPane == null || JTextPaneAppender.javaTextPane == null) {
            return;
        }
        var messageLogEvent = new String[] {
            new String(this.getLayout().toByteArray(event), StandardCharsets.UTF_8)
        };

        var level = event.getLevel().intLevel();
        SwingUtilities.invokeLater(() -> {
            if (messageLogEvent.length == 0) {  // fixes #95664
                return;
            }
            String message = messageLogEvent[0];
            if (level == LogLevelUtil.CONSOLE_JAVA.intLevel()) {
                JTextPaneAppender.javaTextPane.append(message, JTextPaneAppender.ATTRIBUTE_WARN);
            } else if (level == LogLevelUtil.CONSOLE_ERROR.intLevel()) {
                JTextPaneAppender.consoleTextPane.append(message, JTextPaneAppender.ATTRIBUTE_WARN);
            } else if (level == LogLevelUtil.CONSOLE_INFORM.intLevel()) {
                JTextPaneAppender.consoleTextPane.append(message, JTextPaneAppender.ATTRIBUTE_INFORM);
            } else if (level == LogLevelUtil.CONSOLE_SUCCESS.intLevel()) {
                JTextPaneAppender.consoleTextPane.append(message, JTextPaneAppender.ATTRIBUTE_SUCCESS);
            } else if (level != LogLevelUtil.IGNORE.intLevel() && level != Level.ERROR.intLevel()) {  // ignore & stdout when unhandled exception
                JTextPaneAppender.consoleTextPane.append(message, JTextPaneAppender.ATTRIBUTE_ALL);
            }
        });
    }
    
    /**
     * Register the java console.
     */
    public static void registerJavaConsole(SimpleConsoleAdapter javaConsole) {
        JTextPaneAppender.javaTextPane = javaConsole;
    }

    /**
     * Register the default console.
     */
    public static void register(SimpleConsoleAdapter consoleColored) {
        JTextPaneAppender.consoleTextPane = consoleColored;
    }
}