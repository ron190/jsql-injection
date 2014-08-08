/*******************************************************************************
 * Copyhacked (H) 2012-2014.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.textcomponent;

import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.BorderFactory;
import javax.swing.JTextField;

/**
 * A uneditable JTextField decorated with popup menu and border.
 */
@SuppressWarnings("serial")
public class JPopupLabel extends JPopupTextComponent<JTextField> implements JComponentDecorator<JTextField> {
    /**
     * Build a ineditable JTextField in order to create a correct popup menu.
     */
    public JPopupLabel() {
        super(new JTextField() {
            @Override
            public boolean isEditable() {
                return false;
            }
        });

        this.getProxy().addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent arg0) {
                JPopupLabel.this.getProxy().getCaret().setVisible(true);
                JPopupLabel.this.getProxy().getCaret().setSelectionVisible(true);
            }
        });

        this.getProxy().setFont(this.getProxy().getFont().deriveFont(Font.BOLD));
        this.getProxy().setDragEnabled(true);

        this.getProxy().setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
    }
}
