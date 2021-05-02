package com.jsql.view.swing.ui;

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.util.LogLevel;

@SuppressWarnings("serial")
public class BorderRoundBlu extends AbstractBorder {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        
        Graphics2D g2 = (Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        var r = 5;
        
        RoundRectangle2D round = new RoundRectangle2D.Float(x, y, width-1f, height-1f, r, r);
        Container parent = c.getParent();
        
        if (parent!=null) {
            
            g2.setColor(parent.getBackground());
            var corner = new Area(new Rectangle2D.Float(x, y, width, height));
            corner.subtract(new Area(round));
            
            // Fix #42304: NoClassDefFoundError on fill()
            // Fix #42289: UnsatisfiedLinkError on fill()
            try {
                g2.fill(corner);
                
            } catch (NoClassDefFoundError | UnsatisfiedLinkError e) {
                
                LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
            }
        }
        
        g2.setColor(Color.GRAY);
        
        // Fix #55411: NoClassDefFoundError on draw()
        try {
            g2.draw(round);
            
        } catch (NoClassDefFoundError e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
        }
            
        g2.dispose();
    }
    
    @Override
    public Insets getBorderInsets(Component c) {
        
        return new Insets(4, 8, 4, 8);
    }
    
    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        
        insets.left = insets.right = 8;
        insets.top = insets.bottom = 4;
        
        return insets;
    }
}