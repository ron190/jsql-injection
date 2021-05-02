package com.jsql.view.swing.tree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.model.bean.database.AbstractElementDatabase;
import com.jsql.model.bean.database.Column;
import com.jsql.model.bean.database.Database;
import com.jsql.model.bean.database.Table;
import com.jsql.util.LogLevel;
import com.jsql.view.swing.tree.model.AbstractNodeModel;
import com.jsql.view.swing.tree.model.NodeModelColumn;
import com.jsql.view.swing.tree.model.NodeModelDatabase;
import com.jsql.view.swing.tree.model.NodeModelTable;
import com.jsql.view.swing.util.MediatorHelper;

@SuppressWarnings("serial")
public class TreeDatabase extends JTree {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * Map a database element with the corresponding tree node.<br>
     * The injection model send a database element to the view, then
     * the view access its graphic component to update.
     */
    private transient Map<AbstractElementDatabase, DefaultMutableTreeNode> mapNodes = new HashMap<>();

    public TreeDatabase(DefaultMutableTreeNode root) {
        super(root);
    }
    
    public void reloadNodes() {
        
        // I18n of tree empty node
        if (this.isRootVisible()) {
            
            DefaultTreeModel model = (DefaultTreeModel) this.getModel();
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
            model.reload(root);
            this.revalidate();
        }
    }

    public void reset() {
        
        // Tree model for refreshing the tree
        DefaultTreeModel treeModel = (DefaultTreeModel) this.getModel();
        // The tree root
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();

        // Remove tree nodes
        root.removeAllChildren();
        // Refresh the root
        treeModel.nodeChanged(root);
        // Refresh the tree
        treeModel.reload();
        
        this.setRootVisible(true);
    }
    
    public void addColumns(List<Column> columns) {
        
        // Tree model, update the tree (refresh, add node, etc)
        DefaultTreeModel treeModel = (DefaultTreeModel) this.getModel();

        // The table to update
        DefaultMutableTreeNode tableNode = null;

        // Loop into the list of columns
        for (Column column: columns) {
            
            // Create a node model with the column element
            AbstractNodeModel newTreeNodeModel = new NodeModelColumn(column);

            // Create the node
            var newNode = new DefaultMutableTreeNode(newTreeNodeModel);
            
            // Get the parent table
            tableNode = this.getTreeNodeModels().get(column.getParent());
            
            // Fix #1805 : NullPointerException on tableNode.getChildCount()
            if (tableNode != null) {
                
                // Add the column to the table
                treeModel.insertNodeInto(newNode, tableNode, tableNode.getChildCount());
            }
        }

        if (tableNode != null) {
            
            // Open the table node
            this.expandPath(new TreePath(tableNode.getPath()));
            
            // The table has just been search (avoid double check)
            ((AbstractNodeModel) tableNode.getUserObject()).setLoaded(true);
        }
    }
    
    public void addDatabases(List<Database> databases) {
        
        // Tree model, update the tree (refresh, add node, etc)
        DefaultTreeModel treeModel = (DefaultTreeModel) this.getModel();

        // First node in tree
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();

        // Loop into the list of databases
        for (Database database: databases) {
            
            // Create a node model with the database element
            AbstractNodeModel newTreeNodeModel = new NodeModelDatabase(database);
            // Create the node
            var newNode = new DefaultMutableTreeNode(newTreeNodeModel);
            // Save the node
            this.getTreeNodeModels().put(database, newNode);
            // Add the node to the tree
            root.add(newNode);
        }

        // Refresh the tree
        treeModel.reload(root);
        
        // Open the root node
        this.expandPath(new TreePath(root.getPath()));
        this.setRootVisible(false);
    }
    
    public void addTables(List<Table> tables) {
        
        // Tree model, update the tree (refresh, add node, etc)
        DefaultTreeModel treeModel = (DefaultTreeModel) this.getModel();

        // The database to update
        DefaultMutableTreeNode databaseNode = null;

        // Loop into the list of tables
        for (Table table: tables) {
            
            // Create a node model with the table element
            AbstractNodeModel newTreeNodeModel = new NodeModelTable(table);
            
            // Create the node
            var newNode = new DefaultMutableTreeNode(newTreeNodeModel);
            
            // Save the node
            this.getTreeNodeModels().put(table, newNode);

            // Get the parent database
            databaseNode = this.getTreeNodeModels().get(table.getParent());
            
            // Report NullPointerException #1670
            if (databaseNode != null) {
                
                // Add the table to the database
                treeModel.insertNodeInto(newNode, databaseNode, databaseNode.getChildCount());
                
            } else {
                
                LOGGER.log(LogLevel.CONSOLE_ERROR, "Missing database for table {}.", () -> table);
            }
        }

        if (databaseNode != null) {
            
            // Open the database node
            this.expandPath(new TreePath(databaseNode.getPath()));
            
            // The database has just been search (avoid double check)
            ((AbstractNodeModel) databaseNode.getUserObject()).setLoaded(true);
        }
    }
    
    public void createValuesTab(String[][] data, String[] columnNames, AbstractElementDatabase table) {

        // Report NullPointerException #1683
        DefaultMutableTreeNode node = this.getTreeNodeModels().get(table);
        
        if (node != null) {
            
            // Get the node
            AbstractNodeModel progressingTreeNodeModel = (AbstractNodeModel) node.getUserObject();
            
            // Update the progress value of the model, end the progress
            progressingTreeNodeModel.setIndexProgress(table.getChildCount());
            
            // Mark the node model as 'no stop/pause/resume button'
            progressingTreeNodeModel.setRunning(false);
            
            MediatorHelper.tabResults().createValuesTab(data, columnNames, table);
        }
    }
    
    public void endIndeterminateProgess(AbstractElementDatabase dataElementDatabase) {
        
        // Tree model, update the tree (refresh, add node, etc)
        DefaultTreeModel treeModel = (DefaultTreeModel) this.getModel();

        DefaultMutableTreeNode nodeModel = this.getTreeNodeModels().get(dataElementDatabase);
        
        // Fix #1806 : NullPointerException on ...odels().get(dataElementDatabase).getUserObject()
        if (nodeModel != null) {
            
            // Get the node
            AbstractNodeModel progressingTreeNodeModel = (AbstractNodeModel) nodeModel.getUserObject();
            
            // Mark the node model as 'no loading bar'
            progressingTreeNodeModel.setProgressing(false);
            
            // Mark the node model as 'no stop/pause/resume button'
            progressingTreeNodeModel.setRunning(false);
            
            // Update the node
            treeModel.nodeChanged(nodeModel);
        }
    }
    
    public void endProgess(AbstractElementDatabase dataElementDatabase) {
        
        // Tree model, update the tree (refresh, add node, etc)
        DefaultTreeModel treeModel = (DefaultTreeModel) this.getModel();

        // Report NullPointerException #1671
        DefaultMutableTreeNode node = this.getTreeNodeModels().get(dataElementDatabase);
        
        if (node != null) {
            
            // Get the node
            AbstractNodeModel progressingTreeNodeModel = (AbstractNodeModel) node.getUserObject();
            
            // Mark the node model as 'no progress bar'
            progressingTreeNodeModel.setLoading(false);
            
            // Mark the node model as 'no stop/pause/resume button'
            progressingTreeNodeModel.setRunning(false);
            
            // Reset the progress value of the model
            progressingTreeNodeModel.setIndexProgress(0);
            
            // Update the node and progress bar
            treeModel.nodeChanged(node);
        }
    }
    
    public void startIndeterminateProgess(AbstractElementDatabase dataElementDatabase) {
        
        DefaultMutableTreeNode node = this.getTreeNodeModels().get(dataElementDatabase);
        
        // Fix #45540: NullPointerException on node.getUserObject()
        if (node != null) {
            
            // Get the node
            AbstractNodeModel progressingTreeNodeModel = (AbstractNodeModel) node.getUserObject();
            
            // Mark the node model as 'loading'
            progressingTreeNodeModel.setProgressing(true);
            
            // Tree model, update the tree (refresh, add node, etc)
            DefaultTreeModel treeModel = (DefaultTreeModel) this.getModel();
            
            // Update the node
            treeModel.nodeChanged(node);
        }
    }
    
    public void startProgess(AbstractElementDatabase dataElementDatabase) {
        
        DefaultMutableTreeNode node = this.getTreeNodeModels().get(dataElementDatabase);
        
        // Fix rare #73981
        if (node != null) {
            
            // Get the node
            AbstractNodeModel progressingTreeNodeModel = (AbstractNodeModel) node.getUserObject();
            
            // Fix rare Unhandled NullPointerException #66340
            if (progressingTreeNodeModel != null) {
                
                // Mark the node model as 'display progress bar'
                progressingTreeNodeModel.setLoading(true);
            }
            
            // Tree model, update the tree (refresh, add node, etc)
            DefaultTreeModel treeModel = (DefaultTreeModel) this.getModel();
    
            // Update the node
            treeModel.nodeChanged(node);
        }
    }
    
    public void updateProgess(AbstractElementDatabase dataElementDatabase, int dataCount) {
        
        DefaultMutableTreeNode node = this.getTreeNodeModels().get(dataElementDatabase);
        
        // Fix Report #1368: ignore if no element database
        if (node != null) {
            
            // Get the node
            AbstractNodeModel progressingTreeNodeModel = (AbstractNodeModel) node.getUserObject();
            
            // Update the progress value of the model
            progressingTreeNodeModel.setIndexProgress(dataCount);
            
            // Tree model, update the tree (refresh, add node, etc)
            DefaultTreeModel treeModel = (DefaultTreeModel) this.getModel();
            
            // Update the node
            treeModel.nodeChanged(node);
        }
    }
    
    public final Map<AbstractElementDatabase, DefaultMutableTreeNode> getTreeNodeModels() {
        return this.mapNodes;
    }
}
