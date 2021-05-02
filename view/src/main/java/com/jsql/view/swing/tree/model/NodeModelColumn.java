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
package com.jsql.view.swing.tree.model;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JTree;

import com.jsql.model.bean.database.Column;
import com.jsql.util.I18nUtil;
import com.jsql.view.swing.util.UiStringUtil;
import com.jsql.view.swing.util.UiUtil;

/**
 * Column model creating a checkbox.
 * Used by renderer and editor.
 */
public class NodeModelColumn extends NodeModelEmpty {
    
    /**
     * Node as a column model.
     * @param column Element column coming from model
     */
    public NodeModelColumn(Column column) {
        super(column);
    }

    @Override
    public Component getComponent(
        final JTree tree, Object nodeRenderer, final boolean isSelected, boolean isLeaf, boolean hasFocus
    ) {
        
        var checkbox = new JCheckBox(this.toString(), this.isSelected());
        
        checkbox.setText(UiStringUtil.detectUtf8HtmlNoWrap(this.toString()));

        if (isSelected) {
            
            if (hasFocus) {
                
                checkbox.setBackground(UiUtil.COLOR_FOCUS_GAINED);
                checkbox.setBorder(UiUtil.BORDER_FOCUS_GAINED);
                
            } else {
                
                checkbox.setBackground(UiUtil.COLOR_FOCUS_LOST);
                checkbox.setBorder(UiUtil.BORDER_FOCUS_LOST);
            }
        } else {
            
            checkbox.setBackground(Color.WHITE);
            checkbox.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        }
        
        checkbox.setComponentOrientation(ComponentOrientation.getOrientation(I18nUtil.getLocaleDefault()));
        
        return checkbox;
    }
}
