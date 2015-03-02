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
package com.jsql.view.swing.ui;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicSpinnerUI;

/**
 * Better Combox UI.
 */
public class CustomBasicSpinnerUI extends BasicSpinnerUI {
    /**
     * Disable default MetalComboBoxUI
     */
    public CustomBasicSpinnerUI() {
        super();
    }
    
//    @Override
//    public Dimension getPreferredSize(JComponent c) {
//        // TODO Auto-generated method stub
//        return new Dimension(10, 10);
//    }
}
