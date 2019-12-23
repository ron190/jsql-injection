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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.jsql.view.swing.HelperUi;

/**
 * Model for default item used on an empty tree.
 */
public class NodeModelEmpty extends AbstractNodeModel {
    
    /**
     * Flat node for empty tree.
     * @param textNode
     */
    public NodeModelEmpty(String textNode) {
        super(textNode);
    }

    @Override
    public Component getComponent(
        final JTree tree, Object nodeRenderer, final boolean isSelected, boolean isLeaf, boolean hasFocus
    ) {
        DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) nodeRenderer;
        JPanel emptyPanel = new JPanel(new BorderLayout());
        JLabel text = new JLabel(currentNode.getUserObject().toString());
        emptyPanel.add(text);
        text.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        
        if (isSelected) {
            emptyPanel.setBackground(HelperUi.COLOR_FOCUS_GAINED);
            text.setBorder(HelperUi.BORDER_FOCUS_GAINED);
        } else {
            emptyPanel.setBackground(Color.WHITE);
            text.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        }

        return emptyPanel;
    }

    @Override
    protected Icon getLeafIcon(boolean leaf) {
        // No icon for default node
        return null;
    }
    
    @Override
    public void runAction() {
        // Not used
    }
    
    @Override
    protected void buildMenu(JPopupMenu2 tablePopupMenu, TreePath path) {
        // Not used
    }
    
    @Override
    public void showPopup(final DefaultMutableTreeNode currentTableNode, TreePath path, MouseEvent e) {
        // Not used
    }
    
    @Override
    public boolean isPopupDisplayable() {
        // Not used
        return false;
    }
    
}
