package com.jsql.mvc.view.component;

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

public class RoundedCornerBorder extends AbstractBorder {
    
    private static final long serialVersionUID = -8907356713659164971L;
    private boolean drawBorder;
    private int leftRightMargin;
    private int topBottomMargin;
    private Color c = new Color(132,172,221);
    
    public RoundedCornerBorder(int leftRightMargin, int topBottomMargin, boolean drawBorder){
        this.leftRightMargin = leftRightMargin;
        this.topBottomMargin = topBottomMargin;
        this.drawBorder = drawBorder;
    }
    
    public RoundedCornerBorder(int leftRightMargin, int topBottomMargin, boolean drawBorder, Color c){
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
