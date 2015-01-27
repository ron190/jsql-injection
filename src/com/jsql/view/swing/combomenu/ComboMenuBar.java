package com.jsql.view.swing.combomenu;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

@SuppressWarnings("serial")
public class ComboMenuBar extends JMenuBar {
    JMenu menu;
    Dimension preferredSize;

    public ComboMenuBar(JMenu menu) {
        this.menu = menu;

//        this.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 1));
//        this.setBorderPainted(false);
//        UIManager.put("Menu.selectionBackground",
//                UIManager.getColor("Menu.background"));
//        menu.updateUI();

        MenuItemListener listener = new MenuItemListener();
        this.setListener(menu, listener);

        this.add(menu);
    }

    class MenuItemListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JMenuItem item = (JMenuItem)e.getSource();
            menu.setText(item.getText());
            menu.requestFocus();
        }
    }

    private void setListener(JMenuItem item,ActionListener listener) {
        if (item instanceof JMenu) {
            JMenu menu = (JMenu)item;
            int n = menu.getItemCount();
            for (int i=0;i<n;i++) {
                setListener(menu.getItem(i), listener);
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
//            this.setBorder(new EtchedBorder());
            this.setBorderPainted(false);
            this.setIcon(new BlankIcon(null, 11));
            this.setHorizontalTextPosition(JButton.LEFT);
//            this.setFocusPainted(true);
        }

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