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
package com.jsql.view.swing.interaction;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.jsql.model.bean.database.AbstractElementDatabase;
import com.jsql.view.interaction.InteractionCommand;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.tree.model.AbstractNodeModel;

/**
 * Stop refreshing the progress bar of an untracked
 * progression (like colum search).
 */
public class EndIndeterminateProgress implements InteractionCommand {
    
    /**
     * The element in the database tree for which the progress ends.
     */
    private AbstractElementDatabase dataElementDatabase;

    /**
     * @param interactionParams Element to update
     */
    public EndIndeterminateProgress(Object[] interactionParams) {
        this.dataElementDatabase = (AbstractElementDatabase) interactionParams[0];
    }

    @Override
    public void execute() {
        if (MediatorGui.treeDatabase() == null) {
            LOGGER.error("Unexpected unregistered MediatorGui.treeDatabase() in "+ this.getClass());
        }
        
        // Tree model, update the tree (refresh, add node, etc)
        DefaultTreeModel treeModel = (DefaultTreeModel) MediatorGui.treeDatabase().getModel();

        DefaultMutableTreeNode nodeModel = MediatorGui.frame().getTreeNodeModels().get(this.dataElementDatabase);
        
        // Fix #1806 : NullPointerException on ...odels().get(dataElementDatabase).getUserObject()
        if (nodeModel != null) {
            // Get the node
            AbstractNodeModel progressingTreeNodeModel = (AbstractNodeModel) nodeModel.getUserObject();
            // Mark the node model as 'no loading bar'
            progressingTreeNodeModel.setProgressing(false);
            // Mark the node model as 'no stop/pause/resume button'
            progressingTreeNodeModel.setRunning(false);
            
            // Update the node
            treeModel.nodeChanged(nodeModel);
        }
    }
    
}
