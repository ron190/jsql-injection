/*******************************************************************************
 * Copyhacked (H) 2012-2020.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.tab;

import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import com.jsql.view.swing.action.HotkeyUtil;
import com.jsql.view.swing.ui.CustomMetalTabbedPaneUI;

/**
 * Tabs with mousewheel and right click action.
 */
@SuppressWarnings("serial")
public class TabbedPaneWheeled extends JTabbedPane {
    
    /**
     * Create tabs with ctrl-TAB, mousewheel and new UI.
     */
    public TabbedPaneWheeled() {
        
        this(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
    }

    public TabbedPaneWheeled(int tabPlacement, int tabLayoutPolicy) {
        
        super(tabPlacement, tabLayoutPolicy);
        
        // UIManager.put() is not enough
        this.setUI(new CustomMetalTabbedPaneUI());
        
        this.addMouseWheelListener(new TabbedPaneMouseWheelListener());
        
        // Hotkeys ctrl-TAB, ctrl-shift-TAB
        HotkeyUtil.addShortcut(this);
    }

    /**
     * Display popupmenu with a list of tabs.
     */
    public void addMouseClickMenu() {
        this.addMouseListener(new TabMouseAdapter(this));
    }
}
