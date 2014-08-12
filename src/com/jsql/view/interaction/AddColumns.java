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

import com.jsql.model.bean.Column;
import com.jsql.view.MediatorGUI;
import com.jsql.view.tree.AbstractNodeModel;
import com.jsql.view.tree.NodeModelColumn;

/**
 * Add the columns to corresponding table.
 */
public class AddColumns implements IInteractionCommand {
    /**
     * Columns retreived by the view.
     */
    private List<Column> columns;

    /**
     * @param interactionParams List of columns retreived by the Model
     */
    @SuppressWarnings("unchecked")
    public AddColumns(Object[] interactionParams) {
        // Get list of columns from the model
        columns = (List<Column>) interactionParams[0];
    }

    @Override
    public void execute() {
        // Tree model, update the tree (refresh, add node, etc)
        DefaultTreeModel treeModel = (DefaultTreeModel) MediatorGUI.databaseTree().getModel();

        // The table to update
        DefaultMutableTreeNode tableNode = null;

        // Loop into the list of columns
        for (Column column: columns) {
            // Create a node model with the column element
            AbstractNodeModel newTreeNodeModel = new NodeModelColumn(column);

            // Create the node
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newTreeNodeModel);
            // Get the parent table
            tableNode = MediatorGUI.gui().getTreeNodeModels().get(column.getParent());
            // Add the column to the table
            treeModel.insertNodeInto(newNode, tableNode, tableNode.getChildCount());
        }

        if (tableNode != null) {
            // Open the table node
            MediatorGUI.databaseTree().expandPath(new TreePath(tableNode.getPath()));
            // The table has just been search (avoid double check)
            ((AbstractNodeModel) tableNode.getUserObject()).hasBeenSearched = true;
        }
    }
}
