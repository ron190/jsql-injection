package com.jsql.view.swing.console;

import com.jsql.util.LogLevelUtil;
import com.jsql.view.swing.text.JPopupTextPane;
import com.jsql.view.swing.util.MediatorHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * A JTextPane which displays colored strings.
 */
public abstract class AbstractColoredConsole extends JPopupTextPane {
    
    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * Text name of tab.
     */
    private final String tabName;

    /**
     * Create a JTextPane which displays colored strings.
     * @param tabName Text name of tab
     */
    protected AbstractColoredConsole(final String tabName, String placeholder) {
        super(placeholder);
        this.tabName = tabName;
        this.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent focusEvent) {
                AbstractColoredConsole.this.getProxy().getCaret().setVisible(true);
                AbstractColoredConsole.this.getProxy().getCaret().setSelectionVisible(true);
            }
        });
        this.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
    }
    
    protected abstract SimpleAttributeSet getColorAttribute();

    /**
     * Add a string to the end of JTextPane.
     * @param message Text to add
     * @param attribut Font
     */
    public void append(String message, SimpleAttributeSet attribut) {
        try {
            boolean isCaretAtEnd = this.getProxy().getCaretPosition() == this.getProxy().getDocument().getLength();
            
            JScrollPane scrollPane = (JScrollPane) this.getProxy().getParent().getParent();
            JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
            int extent = scrollBar.getModel().getExtent();
            boolean isScrollBarAtEnd = scrollBar.getValue() >= scrollBar.getMaximum() - extent;
            
            var logMessage = message.substring(15);
            var logTimestamp = message.substring(0, 15);
            
            this.getProxy().getDocument().insertString(
                this.getProxy().getDocument().getLength(),
                (this.getProxy().getDocument().getLength() == 0 ? StringUtils.EMPTY : "\n") + logTimestamp,
                this.getColorAttribute()
            );
            
            this.getProxy().getDocument().insertString(
                this.getProxy().getDocument().getLength(),
                logMessage,
                attribut
            );
            
            if (isCaretAtEnd || isScrollBarAtEnd) {
                scrollBar.setValue(scrollBar.getMaximum() + 1);
            }

            var foregroundColor = UIManager.getColor("TextArea.foreground");
            if (attribut == JTextPaneAppender.ATTRIBUTE_WARN) {
                foregroundColor = LogLevelUtil.COLOR_RED;
            } else if (attribut == JTextPaneAppender.ATTRIBUTE_SUCCESS) {
                foregroundColor = LogLevelUtil.COLOR_GREEN;
            }
            
            int tabIndex = MediatorHelper.tabConsoles().indexOfTab(this.tabName);
            if (0 <= tabIndex && tabIndex < MediatorHelper.tabConsoles().getTabCount()) {
                var tabHeader = MediatorHelper.tabConsoles().getTabComponentAt(tabIndex);
                if (MediatorHelper.tabConsoles().getSelectedIndex() != tabIndex) {
                    tabHeader.setFont(tabHeader.getFont().deriveFont(Font.BOLD));
                    if (foregroundColor != UIManager.getColor("TextArea.foreground")) {  // keep tab colored until user clicks
                        tabHeader.setForeground(foregroundColor);
                    }
                }
            }
        } catch (Exception e) {
            // Report #863: exception during report of exception
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, message, e);
        }
    }
}
