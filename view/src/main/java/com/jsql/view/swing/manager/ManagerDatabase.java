/*******************************************************************************
 * Copyhacked (H) 2012-2025.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.manager;

import com.jsql.util.LogLevelUtil;
import com.jsql.view.swing.tree.CellEditorNode;
import com.jsql.view.swing.tree.CellRendererNode;
import com.jsql.view.swing.tree.TreeDatabase;
import com.jsql.view.swing.tree.model.AbstractNodeModel;
import com.jsql.view.swing.tree.model.NodeModelEmpty;
import com.jsql.view.swing.util.I18nViewUtil;
import com.jsql.view.swing.util.MediatorHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Manager to code/decode string in various methods.
 */
public class ManagerDatabase extends JPanel {
    
    private static final Logger LOGGER = LogManager.getRootLogger();

    private TreeDatabase tree;

    /**
     * Create a panel to encode a string.
     */
    public ManagerDatabase() {
        super(new BorderLayout());

        // First node in tree
        AbstractNodeModel nodeModelEmpty = new NodeModelEmpty(I18nViewUtil.valueByKey("DATABASE_EMPTY"));
        var root = new DefaultMutableTreeNode(nodeModelEmpty);
        I18nViewUtil.addComponentForKey("DATABASE_EMPTY", nodeModelEmpty);

        this.tree = new TreeDatabase(root);
        this.tree.setName("treeDatabases");
        MediatorHelper.register(this.tree);

        this.tree.setCellRenderer(new CellRendererNode());
        this.tree.addMouseListener(this.getTreeMouseListener());
        this.tree.addKeyListener(this.getTreeKeyListener());
        this.tree.setCellEditor(new CellEditorNode());
        this.tree.setEditable(true);  // allows repaint nodes
        this.tree.setShowsRootHandles(true);
        this.tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        this.tree.getModel().addTreeModelListener(new TreeModelProgressListener());  // required to repaint progress bar

        this.add(new JScrollPane(this.tree), BorderLayout.CENTER);
    }

    private KeyAdapter getTreeKeyListener() {
        return new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_F2) {
                    DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) ManagerDatabase.this.tree.getLastSelectedPathComponent();
                    if (treeNode != null) {
                        AbstractNodeModel nodeModel = (AbstractNodeModel) treeNode.getUserObject();
                        if (nodeModel != null && nodeModel.getPanel() != null && !nodeModel.isRunning()) {
                            nodeModel.setIsEdited(true);
                        }
                    }
                }
            }
        };
    }

    private MouseAdapter getTreeMouseListener() {
        return new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                int selRow = ManagerDatabase.this.tree.getRowForLocation(event.getX(), event.getY());
                TreePath selPath = ManagerDatabase.this.tree.getPathForLocation(event.getX(), event.getY());
                if (selRow != -1 && event.getClickCount() == 2) {
                    // Fix ArrayIndexOutOfBoundsException on collapsePath()
                    try {
                        if (ManagerDatabase.this.tree.isExpanded(selPath)) {
                            ManagerDatabase.this.tree.collapsePath(selPath);
                        } else {
                            ManagerDatabase.this.tree.expandPath(selPath);
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
                    }
                }
            }
        };
    }
    
    private class TreeModelProgressListener implements TreeModelListener {
        @Override
        public void treeNodesChanged(TreeModelEvent treeModelEvent) {
            if (treeModelEvent == null) {
                return;
            }
            ManagerDatabase.this.tree.firePropertyChange(
                JTree.ROOT_VISIBLE_PROPERTY,
                !ManagerDatabase.this.tree.isRootVisible(),
                ManagerDatabase.this.tree.isRootVisible()
            );
            ManagerDatabase.this.tree.treeDidChange();
        }
        @Override
        public void treeStructureChanged(TreeModelEvent treeModelEvent) {
            // Do nothing
        }
        @Override
        public void treeNodesRemoved(TreeModelEvent treeModelEvent) {
            // Do nothing
        }
        @Override
        public void treeNodesInserted(TreeModelEvent treeModelEvent) {
            // Do nothing
        }
    }
}
