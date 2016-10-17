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

import java.awt.ComponentOrientation;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.lang3.SerializationUtils;

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
    public void addMouseClickMenu() {
        this.addMouseListener(new TabSelectionMouseHandler());
    }

    /**
     * Display popupmenu on right click.
     */
    private class TabSelectionMouseHandler extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                JTabbedPane tabPane = (JTabbedPane) e.getSource();
                JPopupMenu menu = new JPopupMenu();

                for (int position = 0 ; position < MediatorGui.menubar().menuView.getMenuComponentCount() ; position++) {
                    JMenuItem itemMenu = (JMenuItem) SerializationUtils.clone(MediatorGui.menubar().menuView.getMenuComponent(position));
                    menu.add(itemMenu);
                    
                    final int positionFinal = position;
                    itemMenu.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent arg0) {
                            MediatorGui.tabManagers().setSelectedIndex(positionFinal);
                        }
                    });
                }

                menu.show(tabPane, e.getX(), e.getY());
                
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
