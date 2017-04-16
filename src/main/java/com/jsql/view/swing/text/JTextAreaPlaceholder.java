package com.jsql.view.swing.text;

import java.awt.Graphics;

import javax.swing.JTextArea;

import org.apache.log4j.Logger;

/**
 * Textfield with information text displayed when empty.
 */
@SuppressWarnings("serial")
public class JTextAreaPlaceholder extends JTextArea implements InterfaceTextPlaceholder {
	
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
    public JTextAreaPlaceholder(String placeholder, String value) {
        this(placeholder);
        this.setText(value);
    }
    
    /**
     * Create a textfield with hint.
     * @param placeholder Text displayed when empty
     */
    public JTextAreaPlaceholder(String placeholder) {
        this.placeholderText = placeholder;
    }

    @Override
    public void paint(Graphics g) {
        // Fix #6350: ArrayIndexOutOfBoundsException on paint()
        try {
            super.paint(g);
        } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
            LOGGER.error(e.getMessage(), e);
        }
        
        if ("".equals(this.getText())) {
            this.drawPlaceholder(this, g, this.placeholderText);
        }
    }
    
}