/*******************************************************************************
 * Copyhacked (H) 2012-2025.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.tree.model;

import com.jsql.model.bean.database.Column;
import com.jsql.util.I18nUtil;
import com.jsql.view.swing.util.UiStringUtil;

import javax.swing.*;
import java.awt.*;

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
        checkbox.setComponentOrientation(ComponentOrientation.getOrientation(I18nUtil.getLocaleDefault()));
        checkbox.setBackground(
            isSelected ? UIManager.getColor("Tree.selectionBackground") : UIManager.getColor("Tree.background")
        );  // required for transparency
        checkbox.setForeground(  // required by macOS light (opposite text color)
            isSelected ? UIManager.getColor("Tree.selectionForeground") : UIManager.getColor("Tree.foreground")
        );
        return checkbox;
    }
}
