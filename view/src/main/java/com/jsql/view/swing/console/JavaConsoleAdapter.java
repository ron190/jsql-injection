package com.jsql.view.swing.console;

import java.awt.Color;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 * A textpane with color.
 */
@SuppressWarnings("serial")
public class JavaConsoleAdapter extends AbstractColoredConsole {
    
    private SimpleAttributeSet attributeTimestamp = new SimpleAttributeSet();
    
    /**
     * Create adapter for java console.
     * @param tabName Default text
     */
    public JavaConsoleAdapter(String tabName, String placeholder) {
        
        super(tabName, placeholder);
        
        StyleConstants.setForeground(this.attributeTimestamp, Color.RED);
    }

    @Override
    public SimpleAttributeSet getColorAttribute() {
        
        return this.attributeTimestamp;
    }
}
