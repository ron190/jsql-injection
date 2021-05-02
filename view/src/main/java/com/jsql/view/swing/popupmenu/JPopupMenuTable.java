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
package com.jsql.view.swing.popupmenu;

import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.jsql.util.I18nUtil;
import com.jsql.view.swing.menubar.JMenuItemWithMargin;
import com.jsql.view.swing.util.I18nViewUtil;

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

        JMenuItem copyItem = new JMenuItemWithMargin(new ActionCopy());
        copyItem.setText(I18nUtil.valueByKey("CONTEXT_MENU_COPY"));
        I18nViewUtil.addComponentForKey("CONTEXT_MENU_COPY", copyItem);
        copyItem.setMnemonic('C');
        copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        
        this.add(copyItem);

        this.addSeparator();

        JMenuItem selectAllItem = new JMenuItemWithMargin(new ActionSelectAll());
        selectAllItem.setText(I18nUtil.valueByKey("CONTEXT_MENU_SELECT_ALL"));
        I18nViewUtil.addComponentForKey("CONTEXT_MENU_SELECT_ALL", selectAllItem);
        selectAllItem.setMnemonic('A');
        selectAllItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
        
        this.add(selectAllItem);

        // Show menu next mouse pointer
        this.addPopupMenuListener(new PopupMenuListener() {
            
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                
                // Fix #67773: NullPointerException on getLocation()
                if (MouseInfo.getPointerInfo() != null) {
                    
                    JPopupMenuTable.this.setLocation(MouseInfo.getPointerInfo().getLocation());
                }
            }
            
            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                // Do nothing
            }
            
            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                // Do nothing
            }
        });
    }

    public JPopupMenuTable(JTable tableValues, Action actionShowSearchTable) {
        
        this(tableValues);
        
        this.addSeparator();

        JMenuItem search = new JMenuItemWithMargin();
        search.setAction(actionShowSearchTable);
        search.setText("Search...");
        search.setMnemonic('S');
        search.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
        
        this.add(search);
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
            
            var copyEvent = new ActionEvent(
                JPopupMenuTable.this.table,
                ActionEvent.ACTION_PERFORMED,
                "copy"
            );
            
            JPopupMenuTable.this.table.getActionMap().get(copyEvent.getActionCommand()).actionPerformed(copyEvent);
        }
    }
}
