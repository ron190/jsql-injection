package com.jsql.view.swing.manager.util;

import com.jsql.view.swing.combomenu.ArrowIcon;
import com.jsql.view.swing.combomenu.BlankIcon;

import javax.swing.*;
import java.awt.*;

public class ComboMenu extends JMenu {
    
    private final transient ArrowIcon iconRenderer;

    public ComboMenu(String label) {
        
        super(label);
        
        this.iconRenderer = new ArrowIcon(SwingConstants.SOUTH, true);
        this.setBorderPainted(false);
        this.setIcon(new BlankIcon(null, 11));
        this.setHorizontalTextPosition(SwingConstants.RIGHT);
    }

    @Override
    public void paintComponent(Graphics g) {
        
        super.paintComponent(g);
        
        Dimension d = this.getPreferredSize();
        int x = Math.max(0, 10);
        int y = Math.max(0, (d.height - this.iconRenderer.getIconHeight()) / 2 - 1);
        this.iconRenderer.paintIcon(this, g, x, y);
    }
}