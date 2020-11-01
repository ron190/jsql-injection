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

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.jsql.view.swing.text.JPopupTextPane;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;

/**
 * A JTextPane which displays colored strings.
 */
@SuppressWarnings("serial")
public abstract class AbstractColoredConsole extends JPopupTextPane {
    
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
    protected AbstractColoredConsole(final String tabName, String placeholder) {
        
        super(placeholder);
        
        this.tabName = tabName;
        
        this.addFocusListener(new FocusAdapter() {
            
            @Override
            public void focusGained(FocusEvent arg0) {
                
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
            
            String logMessage = message.substring(15);
            String logTimestamp = message.substring(0, 15);
            
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

            Color foregroundColor = Color.BLACK;
            if (attribut == SwingAppender.WARN) {
                foregroundColor = Color.RED;
            } else if (attribut == SwingAppender.DEBUG) {
                foregroundColor = UiUtil.COLOR_GREEN;
            }
            
            int tabIndex = MediatorHelper.tabConsoles().indexOfTab(this.tabName);
            
            if (0 <= tabIndex && tabIndex < MediatorHelper.tabConsoles().getTabCount()) {
                
                Component tabHeader = MediatorHelper.tabConsoles().getTabComponentAt(tabIndex);
                if (MediatorHelper.tabConsoles().getSelectedIndex() != tabIndex) {
                    
                    tabHeader.setFont(tabHeader.getFont().deriveFont(Font.BOLD));
                    tabHeader.setForeground(foregroundColor);
                }
            }
        } catch (Exception e) {
            // Report #863: exception during report of exception
            // Route message to fatal and stderr
            LOGGER.trace(message, e);
        }
    }
}
