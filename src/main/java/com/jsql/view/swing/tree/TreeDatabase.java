package com.jsql.view.swing.tree;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

@SuppressWarnings("serial")
public class TreeDatabase extends JTree {

    public TreeDatabase(DefaultMutableTreeNode root) {
        super(root);
    }

    public void reset() {
        
        // Tree model for refreshing the tree
        DefaultTreeModel treeModel = (DefaultTreeModel) this.getModel();
        // The tree root
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();

        // Remove tree nodes
        root.removeAllChildren();
        // Refresh the root
        treeModel.nodeChanged(root);
        // Refresh the tree
        treeModel.reload();
        
        this.setRootVisible(true);
    }
}
