/*******************************************************************************
 * Copyhacked (H) 2012-2025.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 *******************************************************************************/
package com.jsql.view.swing.text;

import com.jsql.util.LogLevelUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * A JTextArea decorated with popup menu and border.
 */
public class JPopupTextArea extends JPopupTextComponent<JTextArea> implements DecoratorJComponent<JTextArea> {

    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * Build new instance of readonly JTextArea to decorate.
     */
    public JPopupTextArea() {
        this(StringUtils.EMPTY);
    }
    
    /**
     * Build new instance of readonly JTextArea to decorate
     * with a default placeholder.
     */
    public JPopupTextArea(String placeholder) {
        this(new JTextAreaPlaceholder(placeholder) {
            @Override
            public boolean isEditable() {
                return false;
            }
        });
    }

    /**
     * Build new instance of JTextArea to decorate.
     */
    public JPopupTextArea(JTextArea proxy) {
        super(proxy);

        // Side effect: disable caret blink, editable texts must restore blink rate
        this.getProxy().addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent focusEvent) {
                // Fix #95769: IllegalArgumentException on setVisible()
                try {
                    JPopupTextArea.this.getProxy().getCaret().setVisible(true);
                } catch (IllegalArgumentException e) {
                    LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e);
                }
                JPopupTextArea.this.getProxy().getCaret().setSelectionVisible(true);
            }
        });

        this.getProxy().setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        this.getProxy().setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
    }
}
