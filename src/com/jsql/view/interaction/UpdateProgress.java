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
import com.jsql.view.GUI;
import com.jsql.view.tree.NodeModel;

/**
 * Refresh the progress bar of an element in the database tree
 */
public class UpdateProgress implements Interaction{
    // The main View
    private GUI gui;

    // The element in the database tree to refresh
    private ElementDatabase dataElementDatabase;

    // The index of progression
    private int dataCount;

    /**
     * @param mainGUI
     * @param interactionParams Element in the database tree and progression index
     */
    public UpdateProgress(GUI mainGUI, Object[] interactionParams){
        gui = mainGUI;

        dataElementDatabase = (ElementDatabase) interactionParams[0];

        dataCount = (Integer) interactionParams[1];
    }

    /* (non-Javadoc)
     * @see com.jsql.mvc.view.message.ActionOnView#execute()
     */
    public void execute(){
        // Get the node
        NodeModel<?> progressingTreeNodeModel =
                (NodeModel<?>) gui.getNode(dataElementDatabase).getUserObject();
        // Update the progress value of the model
        progressingTreeNodeModel.childUpgradeCount = dataCount;

        // Tree model, update the tree (refresh, add node, etc)
        DefaultTreeModel treeModel = (DefaultTreeModel) gui.databaseTree.getModel();

        // Update the node
        treeModel.nodeChanged(gui.getNode(dataElementDatabase));
    }
}
