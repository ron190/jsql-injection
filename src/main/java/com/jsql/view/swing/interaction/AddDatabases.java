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

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.jsql.model.bean.database.Database;
import com.jsql.view.interaction.InteractionCommand;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.tree.model.AbstractNodeModel;
import com.jsql.view.swing.tree.model.NodeModelDatabase;

/**
 * Add the databases to current injection panel.
 */
public class AddDatabases implements InteractionCommand {
    
    /**
     * Databases retrieved by the view.
     */
    private List<Database> databases;

    /**
     * @param interactionParams List of databases retrieved by the Model
     */
    @SuppressWarnings("unchecked")
    public AddDatabases(Object[] interactionParams) {
        // Get list of databases from the model
        this.databases = (List<Database>) interactionParams[0];
    }

    @Override
    public void execute() {
        if (MediatorGui.treeDatabase() == null) {
            LOGGER.error("Unexpected unregistered MediatorGui.treeDatabase() in "+ this.getClass());
        }
        
        // Tree model, update the tree (refresh, add node, etc)
        DefaultTreeModel treeModel = (DefaultTreeModel) MediatorGui.treeDatabase().getModel();

        // First node in tree
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();

        // Loop into the list of databases
        for (Database database: this.databases) {
            // Create a node model with the database element
            AbstractNodeModel newTreeNodeModel = new NodeModelDatabase(database);
            // Create the node
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newTreeNodeModel);
            // Save the node
            MediatorGui.frame().getTreeNodeModels().put(database, newNode);
            // Add the node to the tree
            root.add(newNode);
        }

        // Refresh the tree
        treeModel.reload(root);
        // Open the root node
        MediatorGui.treeDatabase().expandPath(new TreePath(root.getPath()));
        MediatorGui.treeDatabase().setRootVisible(false);
    }
    
}
