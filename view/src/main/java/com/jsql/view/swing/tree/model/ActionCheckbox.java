package com.jsql.view.swing.tree.model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.jsql.view.swing.util.MediatorHelper;

/**
 * Listener to check or uncheck every children menu items.
 * Usually required from a table node to un/check every columns
 */
public class ActionCheckbox implements ActionListener {
    
    private boolean isCheckboxesSelected;
    private TreePath path;
    
    ActionCheckbox(boolean isCheckboxesSelected, TreePath path) {
        
        this.isCheckboxesSelected = isCheckboxesSelected;
        this.path = path;
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        
        DefaultMutableTreeNode currentTableNode = (DefaultMutableTreeNode) this.path.getLastPathComponent();
        
        AbstractNodeModel currentTableModel = (AbstractNodeModel) currentTableNode.getUserObject();
        
        DefaultTreeModel treeModel = (DefaultTreeModel) MediatorHelper.treeDatabase().getModel();

        int tableChildCount = treeModel.getChildCount(currentTableNode);
        
        for (var i = 0 ; i < tableChildCount ; i++) {
            
            DefaultMutableTreeNode currentChild = (DefaultMutableTreeNode) treeModel.getChild(currentTableNode, i);
            
            if (currentChild.getUserObject() instanceof AbstractNodeModel) {
                
                AbstractNodeModel columnTreeNodeModel = (AbstractNodeModel) currentChild.getUserObject();
                columnTreeNodeModel.setSelected(this.isCheckboxesSelected);
                
                currentTableModel.setContainingSelection(this.isCheckboxesSelected);
            }
        }

        treeModel.nodeChanged(currentTableNode);
    }
}