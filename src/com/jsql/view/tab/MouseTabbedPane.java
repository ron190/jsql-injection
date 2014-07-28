/*******************************************************************************
 * Copyhacked (H) 2012-2013.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.tab;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jsql.view.ActionHandler;

/**
 * Tabs with mousewheel and right click action.
 */
@SuppressWarnings("serial")
public class MouseTabbedPane extends JTabbedPane {

    public MouseTabbedPane(){
        super();

        this.addMouseWheelListener(new TabbedPaneMouseWheelScroller());
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
    
    public void activateMenu(){
    	this.addMouseListener(new TabSelectionMouseHandler());
    }

    private class TabSelectionMouseHandler extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            // we only look at the right button
            if(SwingUtilities.isRightMouseButton(e)) {
                JTabbedPane tabPane = (JTabbedPane)e.getSource();
                JPopupMenu menu = new JPopupMenu();

                int tabCount = tabPane.getTabCount();
                for(int i = 0; i < tabCount; i++) {
                    menu.add(new TabAction(tabPane, i));
                }

                menu.show(tabPane, e.getX(), e.getY());
            }
        }
    }

    private class TabbedPaneMouseWheelScroller implements MouseWheelListener {
        public void mouseWheelMoved(MouseWheelEvent e) {
            JTabbedPane tabPane = (JTabbedPane)e.getSource();
            int dir = -e.getWheelRotation();
            int selIndex = tabPane.getSelectedIndex();
            int maxIndex = tabPane.getTabCount()-1;
            if((selIndex == 0 && dir < 0) || (selIndex == maxIndex && dir > 0)) {
                selIndex = maxIndex - selIndex;
            } else {
                selIndex += dir;
            }
            if(0 <= selIndex && selIndex < tabPane.getTabCount())
                tabPane.setSelectedIndex(selIndex);
        }
    }

    private class TabAction extends AbstractAction {
		private JTabbedPane tabPane;
        private int index;

        public TabAction(JTabbedPane tabPane, int index) {
            super(tabPane.getTitleAt(index), tabPane.getIconAt(index));

            this.tabPane = tabPane;
            this.index   = index;
        }

        public void actionPerformed(ActionEvent e) {
            tabPane.setSelectedIndex(index);
        }
    }
}
