package com.jsql.mvc.view.component.popup;

import java.awt.MouseInfo;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class JPopupTableMenu extends JPopupMenu {
    private static final long serialVersionUID = 1L;
    
    JTable table;
    
    public JPopupTableMenu(JTable _table){
        table = _table;
        
        table.setComponentPopupMenu(this);
        
        JMenuItem copyItem = new JMenuItem();
        copyItem.setAction(new ActionCopy());
        copyItem.setText("Copy");
        this.add( copyItem );
        
        this.addSeparator();
        
        JMenuItem selectAllItem = new JMenuItem();
        selectAllItem.setAction(new ActionSelectAll());
        selectAllItem.setText("Select All");
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
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            table.selectAll();
        }
    }
    
    class ActionCopy extends AbstractAction{
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            ActionEvent copyEvent = new ActionEvent(table, ActionEvent.ACTION_PERFORMED, "copy");
            table.getActionMap().get(copyEvent.getActionCommand()).actionPerformed(copyEvent);
        }
    }
}
