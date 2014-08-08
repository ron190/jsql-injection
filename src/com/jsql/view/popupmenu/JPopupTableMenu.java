/*******************************************************************************
 * Copyhacked (H) 2012-2014.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.popupmenu;

import java.awt.MouseInfo;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.jsql.view.GUITools;

/**
 * Default popup menu and shortcuts for a table.
 */
@SuppressWarnings("serial")
public class JPopupTableMenu extends JPopupMenu {
    /**
     * Table with new menu and shortcut.
     */
    private JTable table;

    /**
     * Create popup menu for this table component.
     * @param table The table receiving the menu
     */
    public JPopupTableMenu(JTable table) {
        this.table = table;

        table.setComponentPopupMenu(this);

        JMenuItem copyItem = new JMenuItem();
        copyItem.setAction(new ActionCopy());
        copyItem.setText("Copy");
        copyItem.setMnemonic('C');
        copyItem.setIcon(GUITools.EMPTY);
        this.add(copyItem);

        this.addSeparator();

        JMenuItem selectAllItem = new JMenuItem();
        selectAllItem.setAction(new ActionSelectAll());
        selectAllItem.setText("Select All");
        selectAllItem.setMnemonic('A');
        selectAllItem.setIcon(GUITools.EMPTY);
        this.add(selectAllItem);

        // Show menu next mouse pointer
        this.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                JPopupTableMenu.this.setLocation(MouseInfo.getPointerInfo().getLocation());
            }
            @Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                // Do nothing
            }
            @Override public void popupMenuCanceled(PopupMenuEvent e) {
                // Do nothing
            }
        });
    }

    /**
     * An action for Select All shortcut.
     */
    private class ActionSelectAll extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            JPopupTableMenu.this.table.selectAll();
        }
    }

    /**
     * An action for Copy shortcut.
     */
    private class ActionCopy extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            ActionEvent copyEvent = new ActionEvent(JPopupTableMenu.this.table, ActionEvent.ACTION_PERFORMED, "copy");
            JPopupTableMenu.this.table.getActionMap().get(copyEvent.getActionCommand()).actionPerformed(copyEvent);
        }
    }
}
