package com.jsql.view.swing.text;

import java.awt.Graphics;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultEditorKit;

import org.apache.log4j.Logger;

import com.jsql.view.swing.HelperUi;
import com.jsql.view.swing.text.action.DeleteNextCharAction;
import com.jsql.view.swing.text.action.DeletePrevCharAction;

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
        
        this.setCaret(new DefaultCaret() {
            @Override
            public void setSelectionVisible(boolean visible) {
                super.setSelectionVisible(true);
            }
        });
        
        this.addFocusListener(new FocusListener() {
            
            @Override
            public void focusLost(FocusEvent e) {
                JTextAreaPlaceholder.this.setSelectionColor(HelperUi.COLOR_FOCUS_LOST);
                JTextAreaPlaceholder.this.revalidate();
                JTextAreaPlaceholder.this.repaint();
            }
            
            @Override
            public void focusGained(FocusEvent e) {
                JTextAreaPlaceholder.this.setSelectionColor(HelperUi.COLOR_FOCUS_GAINED);
                JTextAreaPlaceholder.this.revalidate();
                JTextAreaPlaceholder.this.repaint();
            }
            
        });
        
        this.getActionMap().put(DefaultEditorKit.deletePrevCharAction, new DeletePrevCharAction());
        this.getActionMap().put(DefaultEditorKit.deleteNextCharAction, new DeleteNextCharAction());
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