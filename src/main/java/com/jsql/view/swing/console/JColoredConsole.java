package com.jsql.view.swing.console;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.text.SimpleAttributeSet;

import org.apache.log4j.Logger;

import com.jsql.view.swing.HelperUi;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.text.JPopupTextPane;

/**
 * A JTextPane which displays colored strings.
 */
@SuppressWarnings("serial")
public abstract class JColoredConsole extends JPopupTextPane {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    /**
     * Text name of tab.
     */
    private String tabName;

    /**
     * Create a JTextPane which displays colored strings.
     * @param tabName Text name of tab
     */
    public JColoredConsole(final String tabName, String placeholder) {
        super(placeholder);
        this.tabName = tabName;
        
        this.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent arg0) {
                JColoredConsole.this.getProxy().getCaret().setVisible(true);
                JColoredConsole.this.getProxy().getCaret().setSelectionVisible(true);
            }
        });

        this.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
    }

    /**
     * Add a string to the end of JTextPane.
     * @param message Text to add
     * @param attribut Font
     */
    public void append(String message, SimpleAttributeSet attribut) {
        try {
            boolean isCaretAtEnd = this.getProxy().getCaretPosition() == this.getProxy().getDocument().getLength();
            
            JScrollPane v = (JScrollPane) this.getProxy().getParent().getParent();
            JScrollBar vertical = v.getVerticalScrollBar();
            int extent = vertical.getModel().getExtent();
            boolean isScrollBarAtEnd = vertical.getValue() >= vertical.getMaximum() - extent;
            
            String logMessage = message.substring(15);
            String logTimestamp = message.substring(0, 15);
            
            this.getProxy().getDocument().insertString(
                this.getProxy().getDocument().getLength(),
                (this.getProxy().getDocument().getLength() == 0 ? "" : "\n") + logTimestamp,
                this.getColorAttribute()
            );
            
            this.getProxy().getDocument().insertString(
                this.getProxy().getDocument().getLength(),
                logMessage,
                attribut
            );
            
            if (isCaretAtEnd || isScrollBarAtEnd) {
                vertical.setValue(vertical.getMaximum() + 1);
            }

            Color color = Color.BLACK;
            if (attribut == SwingAppender.WARN) {
                color = Color.RED;
            } else if (attribut == SwingAppender.DEBUG) {
                color = HelperUi.COLOR_GREEN;
            }
            
            int tabIndex = MediatorGui.tabConsoles().indexOfTab(this.tabName);
            if (0 <= tabIndex && tabIndex < MediatorGui.tabConsoles().getTabCount()) {
                Component tabHeader = MediatorGui.tabConsoles().getTabComponentAt(tabIndex);
                if (MediatorGui.tabConsoles().getSelectedIndex() != tabIndex) {
                    tabHeader.setFont(tabHeader.getFont().deriveFont(Font.BOLD));
                    tabHeader.setForeground(color);
                }
            }
        } catch (Exception e) {
            // Report #863: exception during report of exception
            // Route message to fatal and stderr
            LOGGER.trace(message, e);
        }
    }
    
    abstract SimpleAttributeSet getColorAttribute();
    
}
