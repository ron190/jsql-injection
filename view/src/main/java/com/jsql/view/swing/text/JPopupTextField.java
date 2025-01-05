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

import javax.swing.*;

/**
 * A JTextField decorated with popup menu and border.
 */
public class JPopupTextField extends JPopupTextComponent<JTextField> implements DecoratorJComponent<JTextField> {
    
    public JPopupTextField(String placeholder) {
        this(new JTextFieldPlaceholder(placeholder));
    }

    /**
     * Build new instance of JTextField to decorate with default text.
     * @param value Text to display
     */
    public JPopupTextField(String placeholder, String value) {
        this(new JTextFieldPlaceholder(placeholder, value));
    }

    /**
     * Decorate a provided JTextField.
     * @param proxy The JTextField to decorate
     */
    public JPopupTextField(JTextField proxy) {
        super(proxy);
    }
}
