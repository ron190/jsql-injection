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
package com.jsql.view.swing.interaction;

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.jsql.model.bean.Table;
import com.jsql.view.swing.MediatorGUI;
import com.jsql.view.swing.tree.AbstractNodeModel;
import com.jsql.view.swing.tree.NodeModelTable;

/**
 * Add the tables to the corresponding database.
 */
public class AddTables implements IInteractionCommand {
    /**
     * Tables retreived by the view.
     */
    private List<Table> tables;

    /**
     * @param interactionParams List of tables retreived by the Model
     */
    @SuppressWarnings("unchecked")
    public AddTables(Object[] interactionParams) {
        tables = (List<Table>) interactionParams[0];
    }

    @Override
    public void execute() {
        // Tree model, update the tree (refresh, add node, etc)
        DefaultTreeModel treeModel = (DefaultTreeModel) MediatorGUI.databaseTree().getModel();

        // The database to update
        DefaultMutableTreeNode databaseNode = null;

        // Loop into the list of tables
        for (Table table: tables) {
            // Create a node model with the table element
            AbstractNodeModel newTreeNodeModel = new NodeModelTable(table);
            // Create the node
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newTreeNodeModel);
            // Save the node
            MediatorGUI.gui().getTreeNodeModels().put(table, newNode);

            // Get the parent database
            databaseNode = MediatorGUI.gui().getTreeNodeModels().get(table.getParent());
            // Add the table to the database
            treeModel.insertNodeInto(newNode, databaseNode, databaseNode.getChildCount());
        }

        if (databaseNode != null) {
            // Open the database node
            MediatorGUI.databaseTree().expandPath(new TreePath(databaseNode.getPath()));
            // The database has just been search (avoid double check)
            ((AbstractNodeModel) databaseNode.getUserObject()).hasBeenSearched = true;
        }
    }
}
