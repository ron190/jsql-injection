package com.jsql.view.swing.console;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;

/**
 * A textpane with color.
 */
public class SimpleConsoleAdapter extends AbstractColoredConsole {
    
    private final SimpleAttributeSet attributeTimestamp = new SimpleAttributeSet();
    
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
