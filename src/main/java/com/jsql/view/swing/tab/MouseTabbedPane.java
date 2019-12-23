/*******************************************************************************
 * Copyhacked (H) 2012-2016.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.tab;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;

import com.jsql.view.swing.action.ActionHandler;
import com.jsql.view.swing.ui.CustomMetalTabbedPaneUI;

/**
 * Tabs with mousewheel and right click action.
 */
@SuppressWarnings("serial")
public class MouseTabbedPane extends JTabbedPane {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
    /**
     * Create tabs with ctrl-TAB, mousewheel and new UI.
     */
    public MouseTabbedPane() {
        this.addMouseWheelListener(new TabbedPaneMouseWheelScroller());
        // UIManager.put() is not enough
        this.setUI(new CustomMetalTabbedPaneUI());
        this.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        // Give focus on tab change
        this.addChangeListener(changeEvent -> MouseTabbedPane.this.requestFocusInWindow());

        // Hotkeys ctrl-TAB, ctrl-shift-TAB
        ActionHandler.addShortcut(this);
    }

    /**
     * Display popupmenu with a list of tabs.
     */
    public void addMouseClickMenu() {
        this.addMouseListener(new TabSelectionMouseHandler());
    }

    /**
     * Mousewheel allows to navigate to next/previous tab.
     */
    private class TabbedPaneMouseWheelScroller implements MouseWheelListener {
        
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            JTabbedPane tabPane = (JTabbedPane) e.getSource();
            int dir = -e.getWheelRotation();
            int selIndex = tabPane.getSelectedIndex();
            int maxIndex = tabPane.getTabCount() - 1;
            if ((selIndex == 0 && dir < 0) || (selIndex == maxIndex && dir > 0)) {
                selIndex = maxIndex - selIndex;
            } else {
                selIndex += dir;
            }
            if (0 <= selIndex && selIndex < tabPane.getTabCount()) {
                // Fix #54575: NullPointerException on setSelectedIndex()
                try {
                    tabPane.setSelectedIndex(selIndex);
                } catch (NullPointerException err) {
                    LOGGER.error(err, err);
                }
            }
        }
        
    }
    
}
