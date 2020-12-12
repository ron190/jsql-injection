package com.jsql.view.swing.console;

import java.awt.Color;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 * A textpane with color.
 */
@SuppressWarnings("serial")
public class SimpleConsoleAdapter extends AbstractColoredConsole {
    
    private SimpleAttributeSet attributeTimestamp = new SimpleAttributeSet();
    
    /**
     * Create adapter for console.
     * @param tabName Default text
     */
    public SimpleConsoleAdapter(String tabName, String placeholder) {
        
        super(tabName, placeholder);
        
        StyleConstants.setForeground(this.attributeTimestamp, new Color(75, 143, 211));
    }

    @Override
    public SimpleAttributeSet getColorAttribute() {
        
        return this.attributeTimestamp;
    }
}
