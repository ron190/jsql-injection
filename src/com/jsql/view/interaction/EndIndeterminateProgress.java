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
import javax.swing.tree.TreeNode;

import com.jsql.model.bean.ElementDatabase;
import com.jsql.view.GUI;
import com.jsql.view.tree.NodeModel;

/**
 * Stop refreshing the progress bar of a search for which the progression is not tracked (like colum search)
 */
public class EndIndeterminateProgress implements Interaction{
    // The main View
    private GUI gui;

    // The element in the database tree for which the progress ends
    private ElementDatabase dataElementDatabase;

    /**
     * @param mainGUI
     * @param interactionParams Element to update
     */
    public EndIndeterminateProgress(GUI mainGUI, Object[] interactionParams){
        gui = mainGUI;

        dataElementDatabase = (ElementDatabase) interactionParams[0];
    }

    /* (non-Javadoc)
     * @see com.jsql.mvc.view.message.ActionOnView#execute()
     */
    public void execute(){
        // Tree model, update the tree (refresh, add node, etc)
        DefaultTreeModel treeModel = (DefaultTreeModel) gui.databaseTree.getModel();

        // Get the node
        NodeModel<?> progressingTreeNodeModel =
                (NodeModel<?>) gui.getNode(dataElementDatabase).getUserObject();
        // Mark the node model as 'no loading bar'
        progressingTreeNodeModel.hasIndeterminatedProgress = false;
        // Mark the node model as 'no stop/pause/resume button'
        progressingTreeNodeModel.isRunning = false;

        // Update the node
        treeModel.nodeChanged((TreeNode) gui.getNode(dataElementDatabase));
    }
}
