package com.jsql.view.combomenu;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

public class ComboMenuBar extends JMenuBar {

    JMenu menu;
    Dimension preferredSize;

    public ComboMenuBar(JMenu menu) {
        this.menu = menu;

        this.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 1));
        this.setBorderPainted(false);
//        Color color = UIManager.getColor("Menu.selectionBackground");
//        UIManager.put("Menu.selectionBackground",
//                UIManager.getColor("Menu.background"));
//        menu.updateUI();
//        UIManager.put("Menu.selectionBackground", color);

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

//    public void setPreferredSize(Dimension size) {
//        preferredSize = size;
//    }
//
//    public Dimension getPreferredSize() {
//        if (preferredSize == null) {
//            Dimension sd = super.getPreferredSize();
//            Dimension menuD = getItemSize(menu);
//            Insets margin = menu.getMargin();
//            Dimension retD = new Dimension(menuD.width,
//                    margin.top + margin.bottom + menuD.height);
//            menu.setPreferredSize(retD);
//            preferredSize = retD;
//        }
//        return preferredSize;
//    }
//
//    private Dimension getItemSize(JMenu menu) {
//        Dimension d = new Dimension(0,0);
//        int n = menu.getItemCount();
//        for (int i=0;i<n;i++) {
//            Dimension itemD;
//            JMenuItem item = menu.getItem(i);
//            if (item instanceof JMenu) {
//                itemD = getItemSize((JMenu)item);
//            } else if (item != null) {
//                itemD = item.getPreferredSize();
//            } else {
//                itemD = new Dimension(0,0); // separator
//            }
//            d.width = Math.max(d.width, itemD.width );
//            d.height = Math.max(d.height, itemD.height);
//        }
//        return d;
//    }


    public static class ComboMenu extends JMenu {
        ArrowIcon iconRenderer;

        public ComboMenu(String label) {
            super(label);
            iconRenderer = new ArrowIcon(SwingConstants.SOUTH, true);
            this.setBorder(new EtchedBorder());
            this.setBorderPainted(false);
            this.setIcon(new BlankIcon(null, 11));
            this.setHorizontalTextPosition(JButton.LEFT);
            this.setFocusPainted(true);
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

//    labelMenu = new ComboMenu(label);
}