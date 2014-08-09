package com.jsql.view.console;

/**
 * A textpane with color.
 */
@SuppressWarnings("serial")
public class AdapterDefaultColoredConsole extends JColoredConsole {
    /**
     * Create adapter for console.
     * @param tabName Default text
     */
    public AdapterDefaultColoredConsole(String tabName) {
        super(tabName);
    }
}
