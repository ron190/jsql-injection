package com.jsql.view.swing.manager.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

@SuppressWarnings("serial")
public class MenuBarCoder extends JMenuBar {
    
    private JMenu menu;

    private class MenuItemListener implements ActionListener {
        
        @Override
        public void actionPerformed(ActionEvent e) {
            
            JMenuItem item = (JMenuItem) e.getSource();
            MenuBarCoder.this.menu.setText(item.getText());
            
            MenuBarCoder.this.menu.requestFocus();
        }
    }
    
    public MenuBarCoder(JMenu menu) {
        
        this.menu = menu;
        
        var listener = new MenuItemListener();
        this.setListener(menu, listener);
        
        this.add(menu);
    }

    private void setListener(JMenuItem item, ActionListener listener) {
        
        if (item instanceof JMenu) {
            
            JMenu menuContainingItems = (JMenu) item;
            int countItems = menuContainingItems.getItemCount();
            
            for (var i = 0 ; i < countItems ; i++) {
                
                this.setListener(menuContainingItems.getItem(i), listener);
            }
            
        } else if (item != null) { // null means separator
            item.addActionListener(listener);
        }
    }

    public static JMenu createMenu(String label) {
        return new ComboMenu(label);
    }
    
    public String getSelectedItem() {
        return this.menu.getText();
    }
}