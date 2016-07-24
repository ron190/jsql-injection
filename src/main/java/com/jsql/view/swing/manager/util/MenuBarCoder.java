package com.jsql.view.swing.manager.util;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingConstants;

import com.jsql.view.swing.combomenu.ArrowIcon;
import com.jsql.view.swing.combomenu.BlankIcon;

@SuppressWarnings("serial")
public class MenuBarCoder extends JMenuBar {
    JMenu menu;
    Dimension preferredSize;

    public MenuBarCoder(JMenu menu) {
        this.menu = menu;

        MenuItemListener listener = new MenuItemListener();
        this.setListener(menu, listener);

        this.add(menu);
    }

    class MenuItemListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JMenuItem item = (JMenuItem) e.getSource();
            MenuBarCoder.this.menu.setText(item.getText());
            
            
            
            MenuBarCoder.this.menu.requestFocus();
        }
    }

    private void setListener(JMenuItem item, ActionListener listener) {
        if (item instanceof JMenu) {
            JMenu menuContainingItems = (JMenu) item;
            int n = menuContainingItems.getItemCount();
            for (int i = 0 ; i < n ; i++) {
                this.setListener(menuContainingItems.getItem(i), listener);
            }
        } else if (item != null) { // null means separator
            item.addActionListener(listener);
        }
    }

    public String getSelectedItem() {
        return MenuBarCoder.this.menu.getText();
    }

    public static class ComboMenu extends JMenu {
        ArrowIcon iconRenderer;

        public ComboMenu(String label) {
            super(label);
            this.iconRenderer = new ArrowIcon(SwingConstants.SOUTH, true);
            this.setBorderPainted(false);
            this.setIcon(new BlankIcon(null, 11));
            this.setHorizontalTextPosition(JButton.LEFT);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Dimension d = this.getPreferredSize();
            int x = Math.max(0, d.width - this.iconRenderer.getIconWidth() - 3);
            int y = Math.max(0, (d.height - this.iconRenderer.getIconHeight()) / 2 - 1);
            this.iconRenderer.paintIcon(this, g, x, y);
        }
    }

    public static JMenu createMenu(String label) {
        return new ComboMenu(label);
    }
}