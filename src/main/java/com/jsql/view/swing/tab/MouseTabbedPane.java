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

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jsql.view.swing.action.ActionHandler;
import com.jsql.view.swing.ui.CustomMetalTabbedPaneUI;

/**
 * Tabs with mousewheel and right click action.
 */
@SuppressWarnings("serial")
public class MouseTabbedPane extends JTabbedPane {
    /**
     * Create tabs with ctrl-TAB, mousewheel and new UI.
     */
    public MouseTabbedPane() {
        this.addMouseWheelListener(new TabbedPaneMouseWheelScroller());
        // UIManager.put() is not sufficient
        this.setUI(new CustomMetalTabbedPaneUI());
        this.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        // Give focus on tab change
        this.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent arg0) {
                MouseTabbedPane.this.requestFocusInWindow();
            }
        });

        // Hotkeys ctrl-TAB, ctrl-shift-TAB
        ActionHandler.addShortcut(this);
    }

    /**
     * Display popupmenu with a list of tabs.
     */
    public void activateMenu() {
        this.addMouseListener(new TabSelectionMouseHandler());
    }

    /**
     * Display popupmenu on right click.
     */
    private class TabSelectionMouseHandler extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            // we only look at the right button
            if (SwingUtilities.isRightMouseButton(e)) {
                JTabbedPane tabPane = (JTabbedPane) e.getSource();
                JPopupMenu menu = new JPopupMenu();

                int tabCount = tabPane.getTabCount();
                for (int i = 0 ; i < tabCount ; i++) {
                    JMenuItem menuItem = menu.add(new TabAction(tabPane, i));
                    menuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl " + (i + 1)));
                }

                menu.show(tabPane, e.getX(), e.getY());
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

    /**
     * Select the tab selected on the popmenu list of tabs.
     */
    private class TabAction extends AbstractAction {
        private JTabbedPane tabPane;
        private int index;

        public TabAction(JTabbedPane tabPane, int index) {
            super(tabPane.getTitleAt(index), tabPane.getIconAt(index));

            this.tabPane = tabPane;
            this.index   = index;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            tabPane.setSelectedIndex(index);
        }
    }
}
