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
 * Start refreshing the progress bar of an element in the database tree, progression is not tracked (like colum search)
 */
public class StartIndeterminateProgress implements Interaction{
    // The main View
    private GUI gui;

    // The element in the database tree for which the progress starts
    private ElementDatabase dataElementDatabase;

    /**
     * @param mainGUI
     * @param interactionParams Element in the database tree to update
     */
    public StartIndeterminateProgress(GUI mainGUI, Object[] interactionParams){
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
        // Mark the node model as 'loading'
        progressingTreeNodeModel.hasIndeterminatedProgress = true;

        //        treeModel.nodeStructureChanged((TreeNode) treeNodeModels.get(dataElementDatabase)); // update progressbar
        // Update the node
        treeModel.nodeChanged(gui.getNode(dataElementDatabase));
    }
}
