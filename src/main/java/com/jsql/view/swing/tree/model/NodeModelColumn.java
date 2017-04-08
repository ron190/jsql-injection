/*******************************************************************************
 * Copyhacked (H) 2012-2016.
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
import java.awt.Font;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.jsql.i18n.I18n;
import com.jsql.model.bean.database.Column;
import com.jsql.util.StringUtil;

/**
 * Column model creating a checkbox.
 * Used by renderer and editor.
 */
public class NodeModelColumn extends AbstractNodeModel {
	
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
        JCheckBox checkbox = new JCheckBox(this.toString(), this.isSelected);
        checkbox.setFont(
            checkbox.getFont().deriveFont(
                Font.PLAIN | Font.ITALIC,
                checkbox.getFont().getSize()
            )
        );
        
        checkbox.setText(StringUtil.detectUtf8HtmlNoWrap(this.toString()));
        
        checkbox.setBackground(Color.WHITE);
        checkbox.setComponentOrientation(ComponentOrientation.getOrientation(I18n.getLocaleDefault()));
        return checkbox;
    }

    @Override
    Icon getLeafIcon(boolean leaf) {
        // Do nothing
        return null;
    }
    
    @Override
    public void runAction() {
        // Do nothing
    }
    
    @Override
    void buildMenu(JPopupMenu tablePopupMenu, TreePath path) {
        // Do nothing
    }
    
    @Override
    public void showPopup(final DefaultMutableTreeNode currentTableNode, TreePath path, MouseEvent e) {
        // Do nothing
    }
    
    @Override
    public boolean isPopupDisplayable() {
        // Do nothing
        return false;
    }
    
}
