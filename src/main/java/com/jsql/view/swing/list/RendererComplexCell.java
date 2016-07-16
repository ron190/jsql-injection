/*******************************************************************************
 * Copyhacked (H) 2012-2014.
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

import com.jsql.view.swing.HelperUi;

/**
 * Item renderer for JList.
 */
public class RendererComplexCell implements ListCellRenderer<ListItem> {
    /**
     * List component renderer.
     */
    private static DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

    @SuppressWarnings("serial")
    public Component getListCellRendererComponent(
        JList<? extends ListItem> list, ListItem value, int index, boolean isSelected, boolean isFocused
    ) {
        JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(
            list, value, index, isSelected, isFocused
        );

        renderer.setFont(new Font("Segoe UI", Font.PLAIN, renderer.getFont().getSize()));

        if (isSelected && list.isFocusOwner()) {
            renderer.setBackground(HelperUi.SELECTION_BACKGROUND);
        } else if (isSelected && !list.isFocusOwner()) {
            renderer.setBackground(HelperUi.FOCUS_LOST);
        } else {
            renderer.setBackground(Color.WHITE);
        }
        
        if (value.isValidated) {
            renderer.setForeground(new Color(0, 128, 0));
            renderer.setFont(new Font("Segoe UI", Font.BOLD, renderer.getFont().getSize()));
        }

        if (isSelected && list.isFocusOwner()) {
            renderer.setBorder(new LineBorder(HelperUi.BLU_COLOR, 1, false));
        } else if (isSelected && !list.isFocusOwner()) {
            renderer.setBorder(new LineBorder(new Color(218, 218, 218), 1, false));
        } else if (isFocused) {
            renderer.setBorder(BorderFactory.createCompoundBorder( new AbstractBorder() {
                @Override
                public void paintBorder(Component comp, Graphics g, int x, int y, int w, int h) {
                    Graphics2D gg = (Graphics2D) g;
                    gg.setColor(Color.GRAY);
                    gg.setStroke(
                        new BasicStroke(
                            1, 
                            BasicStroke.CAP_BUTT, 
                            BasicStroke.JOIN_BEVEL, 
                            0, 
                            new float[]{1}, 
                            0
                        )
                    );
                    gg.drawRect(x, y, w - 1, h - 1);
                }
            }, BorderFactory.createEmptyBorder(0, 1, 0, 0)));
        } else {
            renderer.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        }

        return renderer;
    }
}
