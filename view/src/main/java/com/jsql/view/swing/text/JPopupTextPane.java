/*******************************************************************************
 * Copyhacked (H) 2012-2020.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 *******************************************************************************/
package com.jsql.view.swing.text;

import java.awt.Cursor;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.BorderFactory;
import javax.swing.JTextPane;

/**
 * A JTextArea decorated with popup menu and border.
 */
@SuppressWarnings("serial")
public class JPopupTextPane extends JPopupTextComponent<JTextPane> implements DecoratorJComponent<JTextPane> {
    
    /**
     * Build new instance of JTextField to decorate.
     * @param placeholder
     */
    public JPopupTextPane(String placeholder) {
        
        this(new JTextPanePlaceholderConsole(placeholder) {
            
            @Override
            public boolean isEditable() {
                
                return false;
            }
        });
    }

    /**
     * Build new instance of JTextArea to decorate.
     */
    public JPopupTextPane(JTextPane proxy) {
        
        super(proxy);

        this.getProxy().addFocusListener(new FocusAdapter() {
            
            @Override
            public void focusGained(FocusEvent arg0) {
                JPopupTextPane.this.getProxy().getCaret().setVisible(true);
                JPopupTextPane.this.getProxy().getCaret().setSelectionVisible(true);
            }
        });

        this.getProxy().setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        this.getProxy().setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
    }
}
