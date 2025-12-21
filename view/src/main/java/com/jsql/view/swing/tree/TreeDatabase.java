package com.jsql.view.swing.tree;

import com.jsql.model.bean.database.AbstractElementDatabase;
import com.jsql.model.bean.database.Column;
import com.jsql.model.bean.database.Database;
import com.jsql.model.bean.database.Table;
import com.jsql.util.LogLevelUtil;
import com.jsql.view.swing.tree.model.AbstractNodeModel;
import com.jsql.view.swing.tree.model.NodeModelColumn;
import com.jsql.view.swing.tree.model.NodeModelDatabase;
import com.jsql.view.swing.tree.model.NodeModelTable;
import com.jsql.view.swing.util.MediatorHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeDatabase extends JTree {
    
    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * Map a database element with the corresponding tree node.<br>
     * The injection model sends a database element to the view, then
     * the view accesses its graphic component for update.
     */
    private final transient Map<AbstractElementDatabase, DefaultMutableTreeNode> mapNodes = new HashMap<>();

    public TreeDatabase(DefaultMutableTreeNode root) {
        super(root);
    }
    
    public void reloadNodes() {
        if (this.isRootVisible()) {  // I18n of tree empty node
            DefaultTreeModel model = (DefaultTreeModel) this.getModel();
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
            model.reload(root);
            this.revalidate();
        }
    }

    public void reset() {
        this.getTreeNodeModels().clear();

        DefaultTreeModel treeModel = (DefaultTreeModel) this.getModel();  // Tree model for refreshing the tree
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();  // The tree root

        root.removeAllChildren();  // Remove tree nodes
        treeModel.nodeChanged(root);  // Refresh the root
        treeModel.reload();  // Refresh the tree
        
        this.setRootVisible(true);
    }
    
    public void addColumns(List<Column> columns) {
        DefaultTreeModel treeModel = (DefaultTreeModel) this.getModel();
        DefaultMutableTreeNode tableNode = null;  // The table to update

        // Loop into the list of columns
        for (Column column: columns) {
            AbstractNodeModel newTreeNodeModel = new NodeModelColumn(column);  // Create a node model with the column element
            var newNode = new DefaultMutableTreeNode(newTreeNodeModel);  // Create the node
            tableNode = this.getTreeNodeModels().get(column.getParent());  // Get the parent table
            
            // Fix #1805 : NullPointerException on tableNode.getChildCount()
            if (tableNode != null) {
                treeModel.insertNodeInto(newNode, tableNode, tableNode.getChildCount());  // Add the column to the table
            }
        }

        if (tableNode != null) {
            this.expandPath(new TreePath(tableNode.getPath()));  // Open the table node
            ((AbstractNodeModel) tableNode.getUserObject()).setLoaded(true);  // The table has just been search (avoid double check)
        }
    }
    
    public void addDatabases(List<Database> databases) {
        DefaultTreeModel treeModel = (DefaultTreeModel) this.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();  // First node in tree

        // Loop into the list of databases
        for (Database database: databases) {
            AbstractNodeModel newTreeNodeModel = new NodeModelDatabase(database);  // Create a node model with the database element
            var newNode = new DefaultMutableTreeNode(newTreeNodeModel);  // Create the node
            this.getTreeNodeModels().put(database, newNode);  // Save the node
            root.add(newNode);  // Add the node to the tree
        }

        // Refresh the tree
        treeModel.reload(root);
        
        // Open the root node
        this.expandPath(new TreePath(root.getPath()));
        this.setRootVisible(false);
    }
    
    public void addTables(List<Table> tables) {
        DefaultMutableTreeNode databaseNode = null;  // The database to update

        for (Table table: tables) {
            AbstractNodeModel newTreeNodeModel = new NodeModelTable(table);  // Create a node model with the table element
            var newNode = new DefaultMutableTreeNode(newTreeNodeModel);  // Create the node
            this.getTreeNodeModels().put(table, newNode);  // Save the node
            databaseNode = this.getTreeNodeModels().get(table.getParent());

            // Report NullPointerException #1670
            if (databaseNode != null) {
                DefaultTreeModel treeModel = (DefaultTreeModel) this.getModel();
                treeModel.insertNodeInto(newNode, databaseNode, databaseNode.getChildCount());  // Add the table to the database
            } else {
                LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Missing database for table {}.", () -> table);
            }
        }

        if (databaseNode != null) {
            this.expandPath(new TreePath(databaseNode.getPath()));  // Open the database node
            ((AbstractNodeModel) databaseNode.getUserObject()).setLoaded(true);  // The database has just been search (avoid double check)
        }
    }
    
    public void createValuesTab(String[][] data, String[] columnNames, AbstractElementDatabase table) {
        // Report NullPointerException #1683
        DefaultMutableTreeNode node = this.getTreeNodeModels().get(table);
        if (node != null) {
            AbstractNodeModel progressingTreeNodeModel = (AbstractNodeModel) node.getUserObject();
            progressingTreeNodeModel.setIndexProgress(table.getChildCount());  // Update the progress value of the model, end the progress
            progressingTreeNodeModel.setRunning(false);  // Mark the node model as 'no stop/pause/resume button'
            MediatorHelper.tabResults().addTabValues(data, columnNames, table);
        }
    }
    
    public void endIndeterminateProgress(AbstractElementDatabase dataElementDatabase) {
        DefaultMutableTreeNode node = this.getTreeNodeModels().get(dataElementDatabase);
        // Fix #1806 : NullPointerException on ...odels().get(dataElementDatabase).getUserObject()
        if (node != null) {
            AbstractNodeModel progressingTreeNodeModel = (AbstractNodeModel) node.getUserObject();
            progressingTreeNodeModel.setProgressing(false);  // Mark the node model as 'no loading bar'
            progressingTreeNodeModel.setRunning(false);  // Mark the node model as 'no stop/pause/resume button'
            DefaultTreeModel treeModel = (DefaultTreeModel) this.getModel();
            treeModel.nodeChanged(node);  // Update the node
        }
    }
    
    public void endProgress(AbstractElementDatabase dataElementDatabase) {
        // Report NullPointerException #1671
        DefaultMutableTreeNode node = this.getTreeNodeModels().get(dataElementDatabase);
        if (node != null) {
            AbstractNodeModel progressingTreeNodeModel = (AbstractNodeModel) node.getUserObject();
            progressingTreeNodeModel.setLoading(false);  // Mark the node model as 'no progress bar'
            progressingTreeNodeModel.setRunning(false);  // Mark the node model as 'no stop/pause/resume button'
            progressingTreeNodeModel.setIndexProgress(0);  // Reset the progress value of the model
            DefaultTreeModel treeModel = (DefaultTreeModel) this.getModel();
            treeModel.nodeChanged(node);  // Update the node and progress bar
        }
    }
    
    public void startIndeterminateProgress(AbstractElementDatabase dataElementDatabase) {
        DefaultMutableTreeNode node = this.getTreeNodeModels().get(dataElementDatabase);
        // Fix #45540: NullPointerException on node.getUserObject()
        if (node != null) {
            AbstractNodeModel progressingTreeNodeModel = (AbstractNodeModel) node.getUserObject();
            progressingTreeNodeModel.setProgressing(true);  // Mark the node model as 'loading'
            DefaultTreeModel treeModel = (DefaultTreeModel) this.getModel();
            treeModel.nodeChanged(node);  // Update the node
        }
    }
    
    public void startProgress(AbstractElementDatabase dataElementDatabase) {
        DefaultMutableTreeNode node = this.getTreeNodeModels().get(dataElementDatabase);
        // Fix rare #73981
        if (node != null) {
            AbstractNodeModel progressingTreeNodeModel = (AbstractNodeModel) node.getUserObject();
            // Fix rare Unhandled NullPointerException #66340
            if (progressingTreeNodeModel != null) {
                progressingTreeNodeModel.setLoading(true);  // Mark the node model as 'display progress bar'
            }
            DefaultTreeModel treeModel = (DefaultTreeModel) this.getModel();
            treeModel.nodeChanged(node);  // Update the node
        }
    }
    
    public void updateProgress(AbstractElementDatabase dataElementDatabase, int dataCount) {
        DefaultMutableTreeNode node = this.getTreeNodeModels().get(dataElementDatabase);
        // Fix Report #1368: ignore if no element database, usually for mock (e.g. metadata, file, shell, list databases)
        if (node != null) {
            AbstractNodeModel progressingTreeNodeModel = (AbstractNodeModel) node.getUserObject();
            progressingTreeNodeModel.setIndexProgress(dataCount);  // Update the progress value of the model
            DefaultTreeModel treeModel = (DefaultTreeModel) this.getModel();
            treeModel.nodeChanged(node);  // Update the node
        }
    }
    
    public final Map<AbstractElementDatabase, DefaultMutableTreeNode> getTreeNodeModels() {
        return this.mapNodes;
    }
}
