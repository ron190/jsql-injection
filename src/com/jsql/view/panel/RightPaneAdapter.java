package com.jsql.view.panel;

import javax.swing.JTabbedPane;
import javax.swing.TransferHandler;

import com.jsql.view.action.ActionHandler;
import com.jsql.view.tab.dnd.DnDTabbedPane;
import com.jsql.view.tab.dnd.TabTransferHandler;

@SuppressWarnings("serial")
public class RightPaneAdapter extends DnDTabbedPane {
	public RightPaneAdapter(){
		this.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		
        TransferHandler handler = new TabTransferHandler();
        this.setTransferHandler(handler);
        
        // Add hotkeys to rootpane ctrl-tab, ctrl-shift-tab, ctrl-w
        ActionHandler.addShortcut(this);
	}
}
