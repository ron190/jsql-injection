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
 * Refresh the progress bar of an element in the database tree
 */
public class UpdateProgress implements InteractionCommand{
    // The element in the database tree to refresh
    private ElementDatabase dataElementDatabase;

    // The index of progression
    private int dataCount;

    /**
     * @param mainGUI
     * @param interactionParams Element in the database tree and progression index
     */
    public UpdateProgress(Object[] interactionParams){
        dataElementDatabase = (ElementDatabase) interactionParams[0];

        dataCount = (Integer) interactionParams[1];
    }

    /* (non-Javadoc)
     * @see com.jsql.mvc.view.message.ActionOnView#execute()
     */
    public void execute(){
        // Get the node
        NodeModel progressingTreeNodeModel =
                (NodeModel) GUIMediator.gui().getNode(dataElementDatabase).getUserObject();
        // Update the progress value of the model
        progressingTreeNodeModel.childUpgradeCount = dataCount;

        // Tree model, update the tree (refresh, add node, etc)
        DefaultTreeModel treeModel = (DefaultTreeModel) GUIMediator.databaseTree().getModel();

        // Update the node
        treeModel.nodeChanged(GUIMediator.gui().getNode(dataElementDatabase));
    }
}
