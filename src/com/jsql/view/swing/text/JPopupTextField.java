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
package com.jsql.view.swing.text;

import javax.swing.BorderFactory;
import javax.swing.JTextField;

import com.jsql.view.swing.HelperGUI;

/**
 * A JTextField decorated with popup menu and border.
 */
@SuppressWarnings("serial")
public class JPopupTextField extends JPopupTextComponent<JTextField> implements DecoratorJComponent<JTextField> {
    /**
     * Build new instance of JTextField to decorate with default text.
     * @param string Text to display
     */
    public JPopupTextField(String hint) {
        this(new PlaceholderTextField(hint));
    }

    /**
     * Build new instance of JTextField to decorate with default text.
     * @param string Text to display
     */
    public JPopupTextField(String hint, String value) {
        this(new PlaceholderTextField(hint, value));
    }

    /**
     * Decorate a provided JTextField.
     * @param proxy The JTextField to decorate
     */
    public JPopupTextField(JTextField proxy) {
        super(proxy);

        this.getProxy().setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(HelperGUI.DEFAULT_BACKGROUND, 2),
                HelperGUI.BLU_ROUND_BORDER));
    }
}
