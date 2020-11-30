/*******************************************************************************
 * Copyhacked (H) 2012-2020.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 *******************************************************************************/
package com.jsql.view.swing.tab;

import java.awt.Component;
import java.awt.Font;

import org.apache.log4j.Logger;

/**
 * Adapt MouseTabbedPane to another class in order to ease Mediator registering.
 */
@SuppressWarnings("serial")
public class TabConsoles extends TabbedPaneWheeled {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    public void highlightTab(String label) {
        
        int tabIndex = this.indexOfTab(label);
        
        // Highlight only if tab not selected and tab exists
        if (this.getSelectedIndex() != tabIndex && 0 <= tabIndex && tabIndex < this.getTabCount()) {
            
            Component tabHeader = this.getTabComponentAt(tabIndex);
            
            // Unhandled ClassCastException #91158 on setFont()
            try {
                tabHeader.setFont(tabHeader.getFont().deriveFont(Font.BOLD));
                
            } catch (ClassCastException e) {
                
                LOGGER.error(e, e);
            }
        }
    }
}
