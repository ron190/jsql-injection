package com.jsql.view.swing.text;

import java.awt.Graphics;

import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.util.LogLevel;
import com.jsql.view.swing.util.UiUtil;

/**
 * Textfield with information text displayed when empty.
 */
@SuppressWarnings("serial")
public class JTextFieldPlaceholder extends JTextField {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    /**
     * Text to display when empty.
     */
    private String placeholderText = StringUtils.EMPTY;
    
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
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
        }
        
        if (this.getText().length() == 0) {
            
            int h = this.getHeight();
            var fm = g.getFontMetrics();

            UiUtil.drawPlaceholder(this, g, this.placeholderText, h / 2 + fm.getAscent() / 2 - 1);
        }
    }

    public void setPlaceholderText(String placeholderText) {
        this.placeholderText = placeholderText;
    }
}