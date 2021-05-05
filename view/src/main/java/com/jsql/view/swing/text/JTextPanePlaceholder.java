package com.jsql.view.swing.text;

import java.awt.Graphics;
import java.util.ConcurrentModificationException;

import javax.swing.JTextPane;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;

import com.jsql.util.LogLevel;
import com.jsql.view.swing.util.UiUtil;

/**
 * Textfield with information text displayed when empty.
 */
@SuppressWarnings("serial")
public class JTextPanePlaceholder extends JTextPane {
    
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
        
        UiUtil.initialize(this);
    }

    @Override
    public void paint(Graphics g) {
        
        // Fix #4012: ArrayIndexOutOfBoundsException on paint()
        // Fix #38546: ConcurrentModificationException on getText()
        // Fix #37872: IndexOutOfBoundsException on getText()
        // Fix #48915: ClassCastException on paint()
        // Unhandled IllegalArgumentException #91471 on paint()
        try {
            super.paint(g);
            
            if (StringUtils.isEmpty(Jsoup.parse(this.getText()).text().trim())) {
                
                UiUtil.drawPlaceholder(this, g, this.placeholderText);
            }
        } catch (IllegalArgumentException | ConcurrentModificationException | IndexOutOfBoundsException | ClassCastException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
        }
    }
}