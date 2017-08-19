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

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.log4j.Logger;

import com.jsql.i18n.I18n;
import com.jsql.view.swing.MediatorGui;
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
     * Display popupmenu on right click.
     */
    class TabSelectionMouseHandler extends MouseAdapter {
        
        @Override
        public void mouseClicked(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                Component componentSource = (Component) e.getSource();
                JPopupMenu menu = new JPopupMenu();

                for (int position = 0 ; position < MediatorGui.menubar().getMenuView().getMenuComponentCount() ; position++) {
                    // Fix #35348: SerializationException on clone()
                    try {
                        JMenuItem itemMenu = (JMenuItem) SerializationUtils.clone(MediatorGui.menubar().getMenuView().getMenuComponent(position));
                        menu.add(itemMenu);
                        
                        final int positionFinal = position;
                        itemMenu.addActionListener(actionEvent -> MediatorGui.tabManagers().setSelectedIndex(positionFinal));
                    } catch (SerializationException ex) {
                        LOGGER.error(ex, ex);
                    }
                }

                menu.show(componentSource, e.getX(), e.getY());
                
                menu.setLocation(
                    ComponentOrientation.getOrientation(I18n.getLocaleDefault()) == ComponentOrientation.RIGHT_TO_LEFT
                    ? e.getXOnScreen() - menu.getWidth()
                    : e.getXOnScreen(),
                    e.getYOnScreen()
                );
            }
        }
        
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
                tabPane.setSelectedIndex(selIndex);
            }
        }
        
    }
    
}
