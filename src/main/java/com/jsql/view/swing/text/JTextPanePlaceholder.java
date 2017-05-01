package com.jsql.view.swing.text;

import java.awt.Graphics;
import java.util.ConcurrentModificationException;

import javax.swing.JTextPane;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;

/**
 * Textfield with information text displayed when empty.
 */
@SuppressWarnings("serial")
public class JTextPanePlaceholder extends JTextPane implements InterfaceTextPlaceholder {
	
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
        this.setText(value);
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
        // Fix #4012: ArrayIndexOutOfBoundsException on paint()
        // Fix #38546: ConcurrentModificationException on getText()
        try {
            super.paint(g);
            if ("".equals(Jsoup.parse(this.getText()).text().trim())) {
                this.drawPlaceholder(this, g, this.placeholderText);
            }
        } catch (ArrayIndexOutOfBoundsException | ConcurrentModificationException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
    
}