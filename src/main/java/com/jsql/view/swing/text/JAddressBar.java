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

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JTextField;

/**
 * A JTextField displaying an icon and buttons.
 */
@SuppressWarnings("serial")
public class JAddressBar extends JPopupTextField implements DecoratorJComponent<JTextField> {
    /**
     * Constructor with default text.
     * @param string The text to display
     */
    public JAddressBar() {
        super(new JTextFieldWithIcon());

        this.getProxy().setPreferredSize(new Dimension(0, 27));
        this.getProxy().setFont(this.getProxy().getFont().deriveFont(Font.PLAIN, this.getProxy().getFont().getSize() + 2));
    }
}
