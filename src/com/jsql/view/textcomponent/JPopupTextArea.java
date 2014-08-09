/*******************************************************************************
 * Copyhacked (H) 2012-2014.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 *******************************************************************************/
package com.jsql.view.textcomponent;

import java.awt.Cursor;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;

/**
 * A JTextArea decorated with popup menu and border.
 */
@SuppressWarnings("serial")
public class JPopupTextArea extends JPopupTextComponent<JTextArea> implements DecoratorJComponent<JTextArea> {
    /**
     * Build new instance of JTextField to decorate.
     */
    public JPopupTextArea() {
        this(new JTextArea() {
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

        this.getProxy().addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent arg0) {
                JPopupTextArea.this.getProxy().getCaret().setVisible(true);
                JPopupTextArea.this.getProxy().getCaret().setSelectionVisible(true);
            }
        });

        this.getProxy().setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        this.getProxy().setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
    }
}
