package com.jsql.view.swing.console;

import com.jsql.util.LogLevelUtil;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

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
        StyleConstants.setForeground(this.attributeTimestamp, LogLevelUtil.COLOR_GRAY);  // timestamp color
    }

    @Override
    public SimpleAttributeSet getColorAttribute() {
        return this.attributeTimestamp;
    }
}
