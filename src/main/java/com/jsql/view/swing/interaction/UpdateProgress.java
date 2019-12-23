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
 * Refresh the progress bar of an element in the database tree.
 */
public class UpdateProgress implements InteractionCommand {
    
    /**
     * The element in the database tree to refresh.
     */
    private AbstractElementDatabase dataElementDatabase;

    /**
     * The index of progression.
     */
    private int dataCount;

    /**
     * @param interactionParams Element in the database tree and progression index
     */
    public UpdateProgress(Object[] interactionParams) {
        this.dataElementDatabase = (AbstractElementDatabase) interactionParams[0];

        this.dataCount = (Integer) interactionParams[1];
    }

    @Override
    public void execute() {
        if (MediatorGui.frame() == null) {
            LOGGER.error("Unexpected unregistered MediatorGui.frame() in "+ this.getClass());
        }
        
        DefaultMutableTreeNode node = MediatorGui.frame().getTreeNodeModels().get(this.dataElementDatabase);
        // Fix Report #1368: ignore if no element database
        if (node != null) {
            // Get the node
            AbstractNodeModel progressingTreeNodeModel = (AbstractNodeModel) node.getUserObject();
            // Update the progress value of the model
            progressingTreeNodeModel.setIndexProgress(this.dataCount);
            
            // Tree model, update the tree (refresh, add node, etc)
            DefaultTreeModel treeModel = (DefaultTreeModel) MediatorGui.treeDatabase().getModel();
            
            // Update the node
            treeModel.nodeChanged(node);
        }
    }
    
}
