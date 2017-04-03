package com.jsql.view.swing.manager.util;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.SwingConstants;

import com.jsql.view.swing.combomenu.ArrowIcon;
import com.jsql.view.swing.combomenu.BlankIcon;

@SuppressWarnings("serial")
public class ComboMenu extends JMenu {
	
    ArrowIcon iconRenderer;

    public ComboMenu(String label) {
        super(label);
        iconRenderer = new ArrowIcon(SwingConstants.SOUTH, true);
        this.setBorderPainted(false);
        this.setIcon(new BlankIcon(null, 11));
        this.setHorizontalTextPosition(JButton.RIGHT);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension d = this.getPreferredSize();
        int x = Math.max(0, 10);
        int y = Math.max(0, (d.height - iconRenderer.getIconHeight()) / 2 - 1);
        iconRenderer.paintIcon(this, g, x, y);
    }
    
}