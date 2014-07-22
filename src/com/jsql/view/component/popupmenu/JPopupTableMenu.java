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
package com.jsql.view.component.popupmenu;

import java.awt.MouseInfo;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.jsql.view.GUITools;

@SuppressWarnings("serial")
public class JPopupTableMenu extends JPopupMenu {
	
    JTable table;
    
    public JPopupTableMenu(JTable table){
        this.table = table;
        
        table.setComponentPopupMenu(this);
        
        JMenuItem copyItem = new JMenuItem();
        copyItem.setAction(new ActionCopy());
        copyItem.setText("Copy");
        copyItem.setMnemonic('C');
        copyItem.setIcon(GUITools.EMPTY);
        this.add( copyItem );
        
        this.addSeparator();
        
        JMenuItem selectAllItem = new JMenuItem();
        selectAllItem.setAction(new ActionSelectAll());
        selectAllItem.setText("Select All");
        selectAllItem.setMnemonic('A');
        selectAllItem.setIcon(GUITools.EMPTY);
        this.add( selectAllItem );
        
        // Show menu next mouse pointer
        this.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                JPopupTableMenu.this.setLocation(MouseInfo.getPointerInfo().getLocation());
            }
            @Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
            @Override public void popupMenuCanceled(PopupMenuEvent e) {}
        });
    }
    
    class ActionSelectAll extends AbstractAction{
        @Override
        public void actionPerformed(ActionEvent e) {
            table.selectAll();
        }
    }
    
    class ActionCopy extends AbstractAction{
        @Override
        public void actionPerformed(ActionEvent e) {
            ActionEvent copyEvent = new ActionEvent(table, ActionEvent.ACTION_PERFORMED, "copy");
            table.getActionMap().get(copyEvent.getActionCommand()).actionPerformed(copyEvent);
        }
    }
}
