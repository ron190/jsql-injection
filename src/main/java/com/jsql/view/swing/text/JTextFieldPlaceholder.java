package com.jsql.view.swing.text;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.JTextField;

import org.apache.log4j.Logger;

/**
 * Textfield with information text displayed when empty.
 */
@SuppressWarnings("serial")
public class JTextFieldPlaceholder extends JTextField {
    
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
    public JTextFieldPlaceholder(String placeholder, String value) {
        this(placeholder);
        this.setText(value);
    }
    
    /**
     * Create a textfield with hint.
     * @param placeholder Text displayed when empty
     */
    public JTextFieldPlaceholder(String placeholder) {
        this.placeholderText = placeholder;
    }

    @Override
    public void paint(Graphics g) {
        try {
            super.paint(g);
        } catch (ClassCastException e) {
            // Fix #4301, ClassCastException: sun.awt.image.BufImgSurfaceData cannot be cast to sun.java2d.xr.XRSurfaceData
            LOGGER.error(e.getMessage(), e);
        }
        
        if (this.getText().length() == 0) {
            int h = this.getHeight();
            int w = this.getWidth();
            ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            Insets ins = this.getInsets();
            int c0 = this.getBackground().getRGB();
            int c1 = this.getForeground().getRGB();
            int m = 0xfefefefe;
            int c2 = ((c0 & m) >>> 1) + ((c1 & m) >>> 1);
            g.setColor(new Color(c2, true));
            g.setFont(this.getFont().deriveFont(Font.ITALIC));
            FontMetrics fm = g.getFontMetrics();
            g.drawString(
                this.placeholderText,
                this.getComponentOrientation() == ComponentOrientation.RIGHT_TO_LEFT
                ? w - (fm.stringWidth(this.placeholderText) + ins.left + 2)
                : ins.left + 2,
                h / 2 + fm.getAscent() / 2 - 1
            );
        }
    }

    public void setPlaceholderText(String placeholderText) {
        this.placeholderText = placeholderText;
    }
    
}