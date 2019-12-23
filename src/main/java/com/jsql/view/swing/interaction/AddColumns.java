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

import com.jsql.model.bean.database.Column;
import com.jsql.view.interaction.InteractionCommand;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.tree.model.AbstractNodeModel;
import com.jsql.view.swing.tree.model.NodeModelColumn;

/**
 * Add the columns to corresponding table.
 */
public class AddColumns implements InteractionCommand {
    
    /**
     * Columns retrieved by the view.
     */
    private List<Column> columns;

    /**
     * @param interactionParams List of columns retrieved by the Model
     */
    @SuppressWarnings("unchecked")
    public AddColumns(Object[] interactionParams) {
        // Get list of columns from the model
        this.columns = (List<Column>) interactionParams[0];
    }

    @Override
    public void execute() {
        if (MediatorGui.treeDatabase() == null) {
            LOGGER.error("Unexpected unregistered MediatorGui.treeDatabase() in "+ this.getClass());
        }
        
        // Tree model, update the tree (refresh, add node, etc)
        DefaultTreeModel treeModel = (DefaultTreeModel) MediatorGui.treeDatabase().getModel();

        // The table to update
        DefaultMutableTreeNode tableNode = null;

        // Loop into the list of columns
        for (Column column: this.columns) {
            // Create a node model with the column element
            AbstractNodeModel newTreeNodeModel = new NodeModelColumn(column);

            // Create the node
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newTreeNodeModel);
            // Get the parent table
            tableNode = MediatorGui.frame().getTreeNodeModels().get(column.getParent());
            
            // Fix #1805 : NullPointerException on tableNode.getChildCount()
            if (tableNode != null) {
                // Add the column to the table
                treeModel.insertNodeInto(newNode, tableNode, tableNode.getChildCount());
            }
        }

        if (tableNode != null) {
            // Open the table node
            MediatorGui.treeDatabase().expandPath(new TreePath(tableNode.getPath()));
            // The table has just been search (avoid double check)
            ((AbstractNodeModel) tableNode.getUserObject()).setLoaded(true);
        }
    }
    
}
