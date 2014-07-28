/*******************************************************************************
 * Copyhacked (H) 2012-2013.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.interaction;

import javax.swing.tree.DefaultTreeModel;

import com.jsql.model.bean.ElementDatabase;
import com.jsql.view.GUIMediator;
import com.jsql.view.tree.NodeModel;

/**
 * Start refreshing the progress bar of an element in the database tree
 */
public class StartProgress implements IInteractionCommand{
    // The element in the database tree for which the progress starts
    private ElementDatabase dataElementDatabase;

    /**
     * @param mainGUI
     * @param interactionParams Element in the database tree to update
     */
    public StartProgress(Object[] interactionParams){
        dataElementDatabase = (ElementDatabase) interactionParams[0];
    }

    /* (non-Javadoc)
     * @see com.jsql.mvc.view.message.ActionOnView#execute()
     */
    public void execute(){
        // Tree model, update the tree (refresh, add node, etc)
        DefaultTreeModel treeModel = (DefaultTreeModel) GUIMediator.databaseTree().getModel();

        // Get the node
        NodeModel progressingTreeNodeModel =
                (NodeModel) GUIMediator.gui().getNode(dataElementDatabase).getUserObject();
        // Mark the node model as 'display progress bar'
        progressingTreeNodeModel.hasProgress = true;

        // Update the node
        treeModel.nodeChanged(GUIMediator.gui().getNode(dataElementDatabase));
    }
}
