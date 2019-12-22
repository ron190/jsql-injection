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

import com.jsql.model.bean.database.Table;
import com.jsql.view.interaction.InteractionCommand;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.tree.model.AbstractNodeModel;
import com.jsql.view.swing.tree.model.NodeModelTable;

/**
 * Add the tables to the corresponding database.
 */
public class AddTables implements InteractionCommand {

    /**
     * Tables retrieved by the view.
     */
    private List<Table> tables;

    /**
     * @param interactionParams List of tables retrieved by the Model
     */
    @SuppressWarnings("unchecked")
    public AddTables(Object[] interactionParams) {
        this.tables = (List<Table>) interactionParams[0];
    }

    @Override
    public void execute() {
        if (MediatorGui.treeDatabase() == null) {
            LOGGER.error("Unexpected unregistered MediatorGui.treeDatabase() in "+ this.getClass());
        }
        
        // Tree model, update the tree (refresh, add node, etc)
        DefaultTreeModel treeModel = (DefaultTreeModel) MediatorGui.treeDatabase().getModel();

        // The database to update
        DefaultMutableTreeNode databaseNode = null;

        // Loop into the list of tables
        for (Table table: this.tables) {
            // Create a node model with the table element
            AbstractNodeModel newTreeNodeModel = new NodeModelTable(table);
            // Create the node
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newTreeNodeModel);
            // Save the node
            MediatorGui.frame().getTreeNodeModels().put(table, newNode);

            // Get the parent database
            databaseNode = MediatorGui.frame().getTreeNodeModels().get(table.getParent());
            
            // Report NullPointerException #1670
            if (databaseNode != null) {
                // Add the table to the database
                treeModel.insertNodeInto(newNode, databaseNode, databaseNode.getChildCount());
            } else {
                LOGGER.warn("Missing database for table "+ table.toString() +".");
            }
        }

        if (databaseNode != null) {
            // Open the database node
            MediatorGui.treeDatabase().expandPath(new TreePath(databaseNode.getPath()));
            // The database has just been search (avoid double check)
            ((AbstractNodeModel) databaseNode.getUserObject()).setLoaded(true);
        }
    }
    
}
