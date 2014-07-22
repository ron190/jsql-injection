/*******************************************************************************
 * Copyhacked (H) 2012-2013.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.component;

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

@SuppressWarnings("serial")
public class RoundBorder extends AbstractBorder {
	
    private boolean drawBorder;
    private int leftRightMargin;
    private int topBottomMargin;
    private Color c = new Color(132,172,221);
    
    public RoundBorder(int leftRightMargin, int topBottomMargin, boolean drawBorder){
        this.leftRightMargin = leftRightMargin;
        this.topBottomMargin = topBottomMargin;
        this.drawBorder = drawBorder;
    }
    
    public RoundBorder(int leftRightMargin, int topBottomMargin, boolean drawBorder, Color c){
        this(leftRightMargin, topBottomMargin, drawBorder);
        this.c = c;
    }
    
    @Override public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int r = 6;
        RoundRectangle2D round = new RoundRectangle2D.Float(x, y, width-1, height-1, r, r);
        Container parent = c.getParent();
        if(parent!=null) {
            g2.setColor(parent.getBackground());
            Area corner = new Area(new Rectangle2D.Float(x, y, width, height));
            corner.subtract(new Area(round));
            g2.fill(corner);
        }
        if(drawBorder)
            g2.setColor(this.c);
        g2.draw(round);
        g2.dispose();
    }
    
    @Override public Insets getBorderInsets(Component c) {
        return new Insets(topBottomMargin, leftRightMargin, topBottomMargin, leftRightMargin);
    }
    
    @Override public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = insets.right = leftRightMargin;
        insets.top = insets.bottom = topBottomMargin;
        return insets;
    }
}
