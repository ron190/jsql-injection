/*******************************************************************************
 * Copyhacked (H) 2012-2020.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.list;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.AbstractBorder;

import com.jsql.view.swing.util.UiUtil;

/**
 * Item renderer for JList.
 */
public class RendererComplexCell implements ListCellRenderer<ItemList> {
    
    /**
     * List component renderer.
     */
    private static DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

    @Override
    public Component getListCellRendererComponent(
        JList<? extends ItemList> list,
        ItemList value,
        int index,
        boolean isSelected,
        boolean isFocused
    ) {
        
        JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, isFocused);

        renderer.setFont(UiUtil.FONT_NON_MONO);

        // setBackground
        if (isSelected) {
            
            if (list.isFocusOwner()) {
                
                renderer.setBackground(UiUtil.COLOR_FOCUS_GAINED);
                
            } else {
                
                renderer.setBackground(UiUtil.COLOR_FOCUS_LOST);
            }
            
        } else {
            
            renderer.setBackground(Color.WHITE);
        }
        
        // setForeground
        if (value.getIsVulnerable()) {
            
            renderer.setForeground(UiUtil.COLOR_GREEN);
            
        } else if (value.getIsDatabaseConfirmed()) {
            
            renderer.setForeground(Color.BLUE);
            
        } else {
            
            // Hardcode black for Mac default is white
            renderer.setForeground(Color.BLACK);
        }

        // setBorder
        if (isSelected) {
            
            if (list.isFocusOwner()) {
                
                renderer.setBorder(UiUtil.BORDER_FOCUS_GAINED);
                
            } else {
                
                renderer.setBorder(UiUtil.BORDER_FOCUS_LOST);
            }
            
        } else if (isFocused) {
            
            renderer.setBorder(BorderFactory.createCompoundBorder(new BorderList(), BorderFactory.createEmptyBorder(0, 1, 0, 0)));
            
        } else {
            
            renderer.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        }

        return renderer;
    }
    
    @SuppressWarnings("serial")
    private class BorderList extends AbstractBorder {
        
        @Override
        public void paintBorder(Component comp, Graphics g, int x, int y, int w, int h) {
            
            Graphics2D g2D = (Graphics2D) g;
            g2D.setColor(Color.GRAY);
            g2D.setStroke(
                new BasicStroke(
                    1,
                    BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_BEVEL,
                    0,
                    new float[]{1},
                    0
                )
            );
            g2D.drawRect(x, y, w - 1, h - 1);
        }
    }
}
