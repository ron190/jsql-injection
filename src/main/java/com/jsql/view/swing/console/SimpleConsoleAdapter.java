package com.jsql.view.swing.console;

/**
 * A textpane with color.
 */
@SuppressWarnings("serial")
public class SimpleConsoleAdapter extends JColoredConsole {
    /**
     * Create adapter for console.
     * @param tabName Default text
     */
    public SimpleConsoleAdapter(String tabName, String placeholder) {
        super(tabName, placeholder);
    }
}
