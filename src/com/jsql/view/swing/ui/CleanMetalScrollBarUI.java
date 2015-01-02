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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalScrollBarUI;

import com.jsql.view.swing.HelperGUI;

/**
 * UI for scrollbars without click button, cleaner and smaller track.
 */
public class CleanMetalScrollBarUI extends MetalScrollBarUI {
    /**
     * Build a zero size button.
     * @return Zero size button
     */
    private JButton createZeroButton() {
        JButton button = new JButton("zero button");
        Dimension zeroDim = new Dimension();
        button.setPreferredSize(zeroDim);
        button.setMinimumSize(zeroDim);
        button.setMaximumSize(zeroDim);
        return button;
    }
    
    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
        // Disable background border and color
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        return new Dimension(12, 12);
    }
    
    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(HelperGUI.SELECTION_BACKGROUND);
        g2.fillRoundRect(r.x,r.y,r.width,r.height, 7,7);
        g2.dispose();
    }        
    
    /**
     * Allows to load Custom ScrollBar UI by UIManager.put("ScrollBarUI", class).
     * @param c Component to customize
     * @return new ScrollBar UI
     */
    public static ComponentUI createUI(JComponent c) {
        return new CleanMetalScrollBarUI();
    }
}
