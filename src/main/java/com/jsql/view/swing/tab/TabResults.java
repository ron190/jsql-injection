/*******************************************************************************
 * Copyhacked (H) 2012-2016.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 *******************************************************************************/
package com.jsql.view.swing.tab;

import javax.swing.JTabbedPane;
import javax.swing.TransferHandler;

import com.jsql.view.swing.action.ActionHandler;
import com.jsql.view.swing.tab.dnd.TabTransferHandler;
import com.jsql.view.swing.tab.dnd.TabbedPaneDnD;

/**
 * TabbedPane containing result injection panels.
 */
@SuppressWarnings("serial")
public class TabResults extends TabbedPaneDnD {
	
    /**
     * Create the panel containing injection results.
     */
    public TabResults() {
        this.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        TransferHandler handler = new TabTransferHandler();
        this.setTransferHandler(handler);

        // Add hotkeys to rootpane ctrl-tab, ctrl-shift-tab, ctrl-w
        ActionHandler.addShortcut(this);
    }
    
}
