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
package com.jsql.view.ui;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalScrollBarUI;

/**
 * UI for scrollbars without click button, cleaner and smaller track.
 */
public class CleanMetalScrollBarUI extends MetalScrollBarUI {

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }
    
    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
    }
    
    /**
     * Build a zero size button.
     * @return Zero size button
     */
    protected JButton createZeroButton() {
        JButton button = new JButton("zero button");
        Dimension zeroDim = new Dimension();
        button.setPreferredSize(zeroDim);
        button.setMinimumSize(zeroDim);
        button.setMaximumSize(zeroDim);
        return button;
    }
    
    @Override 
    protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
        // Disable background border and color
    }
    
    @Override
    public Dimension getPreferredSize(JComponent c) {
        return new Dimension(12, 12);
    }
    
    /**
     * Used by UIManager.put("ScrollBarUI", class).
     * @param c
     * @return new UI
     */
    public static ComponentUI createUI(JComponent c){
        return new CleanMetalScrollBarUI();
    }
}