package com.jsql.view.swing.combomenu;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class ComboMenuBar extends JMenuBar {
    JMenu menu;
    Dimension preferredSize;

    public ComboMenuBar(JMenu menu) {
        this.menu = menu;

        MenuItemListener listener = new MenuItemListener();
        this.setListener(menu, listener);

        this.add(menu);
    }

    class MenuItemListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JMenuItem item = (JMenuItem)e.getSource();
            menu.setText(item.getText());
            menu.requestFocus();
        }
    }

    private void setListener(JMenuItem item,ActionListener listener) {
        if (item instanceof JMenu) {
            JMenu menuContainingItems = (JMenu) item;
            int n = menuContainingItems.getItemCount();
            for (int i = 0 ; i < n ; i++) {
                setListener(menuContainingItems.getItem(i), listener);
            }
        } else if (item != null) { // null means separator
            item.addActionListener(listener);
        }
    }

    public String getSelectedItem() {
        return menu.getText();
    }

    public static class ComboMenu extends JMenu {
        ArrowIcon iconRenderer;

        public ComboMenu(String label) {
            super(label);
            iconRenderer = new ArrowIcon(SwingConstants.SOUTH, true);
            this.setBorderPainted(false);
            this.setIcon(new BlankIcon(null, 11));
            this.setHorizontalTextPosition(JButton.LEFT);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Dimension d = this.getPreferredSize();
            int x = Math.max(0, d.width - iconRenderer.getIconWidth() -3);
            int y = Math.max(0, (d.height - iconRenderer.getIconHeight())/2 -2);
            iconRenderer.paintIcon(this,g, x,y);
        }
    }

    public static JMenu createMenu(String label) {
        return new ComboMenu(label);
    }
}