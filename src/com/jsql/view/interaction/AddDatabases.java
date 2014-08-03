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
package com.jsql.view.interaction;

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.jsql.model.bean.Database;
import com.jsql.view.GUIMediator;
import com.jsql.view.tree.NodeModel;
import com.jsql.view.tree.NodeModelDatabase;

/**
 * Add the databases to current injection panel
 */
public class AddDatabases implements IInteractionCommand{
    // Databases retreived by the view
    private List<Database> databases;

    /**
     * @param interactionParams List of databases retreived by the Model
     */
    @SuppressWarnings("unchecked")
	public AddDatabases(Object[] interactionParams){
        // Get list of databases from the model
        databases = (List<Database>) interactionParams[0];
    }

    public void execute(){
        // Tree model, update the tree (refresh, add node, etc)
        DefaultTreeModel treeModel = (DefaultTreeModel) GUIMediator.databaseTree().getModel();

        // First node in tree
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();

        // Loop into the list of databases
        for(Database database: databases){
            // Create a node model with the database element
            NodeModel newTreeNodeModel = new NodeModelDatabase(database);
            // Create the node
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode( newTreeNodeModel );
            // Save the node
            GUIMediator.gui().putNode(database, newNode);
            // Add the node to the tree
            root.add(newNode);
        }

        // Refresh the tree
        treeModel.reload(root);
        // Open the root node
        GUIMediator.databaseTree().expandPath( new TreePath(root.getPath()) );
        GUIMediator.databaseTree().setRootVisible(false);
    }
}
