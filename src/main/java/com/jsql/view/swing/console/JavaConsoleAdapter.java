package com.jsql.view.swing.console;

/**
 * A textpane with color.
 */
@SuppressWarnings("serial")
public class JavaConsoleAdapter extends JColoredConsole {
	
    /**
     * Create adapter for java console.
     * @param tabName Default text
     */
    public JavaConsoleAdapter(String tabName, String placeholder) {
        super(tabName, placeholder);
    }
    
}
