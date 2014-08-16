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

import com.jsql.i18n.I18n;
import com.jsql.view.ToolsGUI;

/**
 * Default popup menu and shortcuts for a table.
 */
@SuppressWarnings("serial")
public class JPopupMenuTable extends JPopupMenu {
    /**
     * Table with new menu and shortcut.
     */
    private JTable table;

    /**
     * Create popup menu for this table component.
     * @param table The table receiving the menu
     */
    public JPopupMenuTable(JTable table) {
        this.table = table;

        table.setComponentPopupMenu(this);

        JMenuItem copyItem = new JMenuItem();
        copyItem.setAction(new ActionCopy());
        copyItem.setText(I18n.COPY);
        copyItem.setMnemonic('C');
        copyItem.setIcon(ToolsGUI.EMPTY);
        this.add(copyItem);

        this.addSeparator();

        JMenuItem selectAllItem = new JMenuItem();
        selectAllItem.setAction(new ActionSelectAll());
        selectAllItem.setText(I18n.SELECT_ALL);
        selectAllItem.setMnemonic('A');
        selectAllItem.setIcon(ToolsGUI.EMPTY);
        this.add(selectAllItem);

        // Show menu next mouse pointer
        this.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                JPopupMenuTable.this.setLocation(MouseInfo.getPointerInfo().getLocation());
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
            JPopupMenuTable.this.table.selectAll();
        }
    }

    /**
     * An action for Copy shortcut.
     */
    private class ActionCopy extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            ActionEvent copyEvent = new ActionEvent(JPopupMenuTable.this.table, ActionEvent.ACTION_PERFORMED, "copy");
            JPopupMenuTable.this.table.getActionMap().get(copyEvent.getActionCommand()).actionPerformed(copyEvent);
        }
    }
}
