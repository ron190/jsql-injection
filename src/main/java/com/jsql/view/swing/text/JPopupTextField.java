/*******************************************************************************
 * Copyhacked (H) 2012-2016.
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

import com.jsql.view.swing.HelperUi;

/**
 * A JTextField decorated with popup menu and border.
 */
@SuppressWarnings("serial")
public class JPopupTextField extends JPopupTextComponent<JTextField> implements DecoratorJComponent<JTextField> {
    
    /**
     * Build new instance of JTextField to decorate with default text.
     * @param string Text to display
     */
    public JPopupTextField(String placeholder) {
        this(new JTextFieldPlaceholder(placeholder));
    }

    /**
     * Build new instance of JTextField to decorate with default text.
     * @param string Text to display
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

        this.getProxy().setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(HelperUi.COLOR_DEFAULT_BACKGROUND, 2),
                HelperUi.BORDER_BLU
            )
        );
    }
    
}
