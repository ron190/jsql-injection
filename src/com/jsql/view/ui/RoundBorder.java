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
package com.jsql.view.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.border.AbstractBorder;

/**
 * A border displayed with round corner, custom color or hidden border.
 */
@SuppressWarnings("serial")
public class RoundBorder extends AbstractBorder {
    /**
     * True if border is visible, false otherwise.
     */
    private boolean isVisible = true;
    
    /**
     * Margin on left and right.
     */
    private int leftRightMargin;
    
    /**
     * Margin on top and bottom.
     */
    private int topBottomMargin;
    
    /**
     * Default border color.
     */
    private Color color = new Color(132, 172, 221);

    /**
     * Create border with margin ; visibility is hidden or normal.
     * @param leftRightMargin Margin on left and right
     * @param topBottomMargin Margin on top and bottom
     * @param isVisible True if border is visible, false otherwise
     */
    public RoundBorder(int leftRightMargin, int topBottomMargin, boolean isVisible) {
        super();

        this.leftRightMargin = leftRightMargin;
        this.topBottomMargin = topBottomMargin;
        this.isVisible = isVisible;
    }

    /**
     * Create border with margin and custom color.
     * @param leftRightMargin Margin on left and right
     * @param topBottomMargin Margin on top and bottom
     * @param color Color of the border
     */
    public RoundBorder(int leftRightMargin, int topBottomMargin, Color color) {
        this(leftRightMargin, topBottomMargin, true);
        this.color = color;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int r = 6;
        RoundRectangle2D round = new RoundRectangle2D.Float(x, y, width - 1, height - 1, r, r);
        Container parent = c.getParent();
        if (parent != null) {
            g2.setColor(parent.getBackground());
            Area corner = new Area(new Rectangle2D.Float(x, y, width, height));
            corner.subtract(new Area(round));
            g2.fill(corner);
        }
        if (isVisible) {
            g2.setColor(this.color);
        }
        g2.draw(round);
        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(topBottomMargin, leftRightMargin, topBottomMargin, leftRightMargin);
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = insets.right = leftRightMargin;
        insets.top = insets.bottom = topBottomMargin;
        return insets;
    }
}
