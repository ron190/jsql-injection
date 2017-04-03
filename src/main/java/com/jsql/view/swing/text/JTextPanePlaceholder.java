package com.jsql.view.swing.text;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.JTextPane;

import org.apache.log4j.Logger;

/**
 * Textfield with information text displayed when empty.
 */
@SuppressWarnings("serial")
public class JTextPanePlaceholder extends JTextPane {
	
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
    /**
     * Text to display when empty.
     */
    private String placeholderText = "";
    
    /**
     * Create a textfield with hint and default value.
     * @param placeholder Text displayed when empty
     * @param value Default value
     */
    public JTextPanePlaceholder(String placeholder, String value) {
        this(placeholder);
        setText(value);
    }
    
    /**
     * Create a textfield with hint.
     * @param placeholder Text displayed when empty
     */
    public JTextPanePlaceholder(String placeholder) {
        this.placeholderText = placeholder;
    }

    @Override
    public void paint(Graphics g) {
        // Fix #4012
        try {
            super.paint(g);
        } catch (ArrayIndexOutOfBoundsException e) {
            LOGGER.error("Handled Exception: "+ e, e);
        }
        
        // Default empty JTextPane is 110 units long, so to get placeholder we need to test for this value
        if (this.getText().length() == 110 || this.getText().length() == 0) {
            int w = this.getWidth();
            
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            Insets ins = getInsets();
            FontMetrics fm = g.getFontMetrics();
            
            int c0 = getBackground().getRGB();
            int c1 = getForeground().getRGB();
            int m = 0xfefefefe;
            int c2 = ((c0 & m) >>> 1) + ((c1 & m) >>> 1);
            
            g.setColor(new Color(c2, true));
            g.setFont(this.getFont().deriveFont(Font.ITALIC));
            
            g.drawString(
                this.placeholderText, 
                this.getComponentOrientation() == ComponentOrientation.RIGHT_TO_LEFT
                    ? w - (fm.stringWidth(this.placeholderText) + ins.left + 2)
                    : ins.left + 2,
                fm.getAscent() + 2
            );
        }
    }
    
}