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
package com.jsql.view.dnd.list;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.AbstractBorder;
import javax.swing.border.LineBorder;

import com.jsql.view.GUITools;

public class ComplexCellRenderer implements ListCellRenderer {
    JList<ListItem> myList;
    int[] mouseOver;
    
    public ComplexCellRenderer(JList<ListItem> myList, int[] mouseOver){
        this.myList = myList;
        this.mouseOver = mouseOver;
    }
    
    protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

    public Component getListCellRendererComponent(JList list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {
        JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index,
                isSelected, cellHasFocus);

        renderer.setFont(new Font("Segoe UI", Font.PLAIN, renderer.getFont().getSize()));

        if(isSelected&&myList.isFocusOwner())
            renderer.setBackground(GUITools.SELECTION_BACKGROUND);
        else if(mouseOver[0] == index)
            renderer.setBackground(new Color(237,245,255));
        else if(isSelected&&!myList.isFocusOwner())
            renderer.setBackground(new Color(248,249,249));
        else
            renderer.setBackground(Color.WHITE);

        if(isSelected&&myList.isFocusOwner())
            renderer.setBorder(new LineBorder(new Color(132,172,221), 1, true));
        else if(mouseOver[0] == index)
            renderer.setBorder(new LineBorder(new Color(185,215,252), 1, true));
        else if(isSelected&&!myList.isFocusOwner())
            renderer.setBorder(new LineBorder(new Color(218,218,218), 1, true));
        else if(cellHasFocus)
            renderer.setBorder(BorderFactory.createCompoundBorder( new AbstractBorder() {
                @Override
                public void paintBorder(Component comp, Graphics g, int x, int y, int w, int h) {
                    Graphics2D gg = (Graphics2D) g;
                    gg.setColor(Color.GRAY);
                    gg.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{1}, 0));
                    gg.drawRect(x, y, w - 1, h - 1);
                }
            },BorderFactory.createEmptyBorder(0, 1, 0, 0)));
        else
            renderer.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

        return renderer;
    }
}
