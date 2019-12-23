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
 * Start refreshing the progress bar of an element in the database tree.
 */
public class StartProgress implements InteractionCommand {
    
    /**
     * The element in the database tree for which the progress starts.
     */
    private AbstractElementDatabase dataElementDatabase;

    /**
     * @param interactionParams Element in the database tree to update
     */
    public StartProgress(Object[] interactionParams) {
        this.dataElementDatabase = (AbstractElementDatabase) interactionParams[0];
    }

    @Override
    public void execute() {
        if (MediatorGui.treeDatabase() == null) {
            LOGGER.error("Unexpected unregistered MediatorGui.treeDatabase() in "+ this.getClass());
        }
        
        // Tree model, update the tree (refresh, add node, etc)
        DefaultTreeModel treeModel = (DefaultTreeModel) MediatorGui.treeDatabase().getModel();

        DefaultMutableTreeNode node = MediatorGui.frame().getTreeNodeModels().get(this.dataElementDatabase);
        
        // Get the node
        AbstractNodeModel progressingTreeNodeModel = (AbstractNodeModel) node.getUserObject();
        // Mark the node model as 'display progress bar'
        progressingTreeNodeModel.setLoading(true);

        // Update the node
        treeModel.nodeChanged(node);
    }
    
}
