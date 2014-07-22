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

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.jsql.model.bean.Table;
import com.jsql.view.GUIMediator;
import com.jsql.view.tree.NodeModel;
import com.jsql.view.tree.NodeModelTable;

/**
 * Add the tables to the corresponding database
 */
public class AddTables implements InteractionCommand{
    // Tables retreived by the view
    private List<Table> tables;

    /**
     * @param interactionParams List of tables retreived by the Model
     */
    @SuppressWarnings("unchecked")
	public AddTables(Object[] interactionParams){
        tables = (List<Table>) interactionParams[0];
    }

    /* (non-Javadoc)
     * @see com.jsql.mvc.view.message.ActionOnView#execute()
     */
    public void execute(){
        // Tree model, update the tree (refresh, add node, etc)
        DefaultTreeModel treeModel = (DefaultTreeModel) GUIMediator.databaseTree().getModel();

        // The database to update
        DefaultMutableTreeNode databaseNode = null;

        // Loop into the list of tables
        for(Table table: tables){
            // Create a node model with the table element
//            NodeModel<Table> newTreeNodeModel = new NodeModel<Table>(table);
            NodeModel newTreeNodeModel = new NodeModelTable(table);
            // Create the node
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode( newTreeNodeModel );
            // Save the node
            GUIMediator.gui().putNode(table, newNode);

            // Get the parent database
            databaseNode = GUIMediator.gui().getNode(table.getParent());
            // Add the table to the database
            treeModel.insertNodeInto(newNode, databaseNode, databaseNode.getChildCount());
        }

        if(databaseNode != null){
            // Open the database node
        	GUIMediator.databaseTree().expandPath( new TreePath(databaseNode.getPath()) );
            // The database has just been search (avoid double check)
            ((NodeModel) databaseNode.getUserObject()).hasBeenSearched = true;
        }
    }
}
