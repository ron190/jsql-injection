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
package com.jsql.view.swing.interaction;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.jsql.model.bean.AbstractElementDatabase;
import com.jsql.view.swing.MediatorGUI;
import com.jsql.view.swing.tree.model.AbstractNodeModel;

/**
 * Stop the refreshing of progress bar.
 */
public class EndProgress implements InteractionCommand {
    /**
     * The element in the database tree for which the progress ends.
     */
    private AbstractElementDatabase dataElementDatabase;

    /**
     * @param interactionParams Element to update
     */
    public EndProgress(Object[] interactionParams) {
        dataElementDatabase = (AbstractElementDatabase) interactionParams[0];
    }

    @Override
    public void execute() {
        // Tree model, update the tree (refresh, add node, etc)
        DefaultTreeModel treeModel = (DefaultTreeModel) MediatorGUI.databaseTree().getModel();

        // Report NullPointerException #1671 
        DefaultMutableTreeNode node = MediatorGUI.jFrame().getTreeNodeModels().get(dataElementDatabase);
        
        if (node != null) {
            // Get the node
            AbstractNodeModel progressingTreeNodeModel = (AbstractNodeModel) node.getUserObject();
            // Mark the node model as 'no progress bar'
            progressingTreeNodeModel.hasProgress = false;
            // Mark the node model as 'no stop/pause/resume button'
            progressingTreeNodeModel.isRunning = false;
            // Reset the progress value of the model
            progressingTreeNodeModel.childUpgradeCount = 0;
            
            // Update the node and progressbar
            treeModel.nodeChanged(node); 
        }
    }
}
