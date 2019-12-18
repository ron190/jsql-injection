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
package com.jsql.view.swing.manager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.jsql.view.i18n.I18nView;
import com.jsql.view.swing.HelperUi;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.tree.CellEditorNode;
import com.jsql.view.swing.tree.CellRendererNode;
import com.jsql.view.swing.tree.model.AbstractNodeModel;
import com.jsql.view.swing.tree.model.NodeModelEmpty;

/**
 * Manager to code/uncode string in various methods.
 */
@SuppressWarnings("serial")
public class ManagerDatabase extends JPanel implements Manager {

    private JTree tree;

    /**
     * Create a panel to encode a string.
     */
    public ManagerDatabase() {
        super(new BorderLayout());

        // First node in tree
        AbstractNodeModel nodeModelEmpty = new NodeModelEmpty(I18nView.valueByKey("DATABASE_EMPTY"));
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(nodeModelEmpty);
        I18nView.addComponentForKey("DATABASE_EMPTY", nodeModelEmpty);
        
        this.tree = new JTree(root);
        MediatorGui.register(this.tree);

        // Graphic manager for components
        this.tree.setCellRenderer(new CellRendererNode());

        this.tree.addFocusListener(new FocusListener() {

            @Override
            public void focusLost(FocusEvent e) {
                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) ManagerDatabase.this.tree.getLastSelectedPathComponent();
                if (treeNode != null) {
                    AbstractNodeModel nodeModel = (AbstractNodeModel) treeNode.getUserObject();
                    if (nodeModel != null && nodeModel.getPanel() != null) {
                        nodeModel.getPanel().getLabel().setBackground(HelperUi.COLOR_FOCUS_LOST);
                        nodeModel.getPanel().getLabel().setBorder(HelperUi.BORDER_FOCUS_LOST);
                    }
                }
            }

            @Override
            public void focusGained(FocusEvent e) {
                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) ManagerDatabase.this.tree.getLastSelectedPathComponent();
                if (treeNode != null) {
                    AbstractNodeModel nodeModel = (AbstractNodeModel) treeNode.getUserObject();
                    if (nodeModel != null && nodeModel.getPanel() != null) {
                        nodeModel.getPanel().getLabel().setBackground(HelperUi.COLOR_FOCUS_GAINED);
                        nodeModel.getPanel().getLabel().setBorder(HelperUi.BORDER_FOCUS_GAINED);
                    }
                }
            }

        });

        this.tree.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                int selRow = ManagerDatabase.this.tree.getRowForLocation(e.getX(), e.getY());
                TreePath selPath = ManagerDatabase.this.tree.getPathForLocation(e.getX(), e.getY());
                if (selRow != -1 && e.getClickCount() == 2) {
                    if (ManagerDatabase.this.tree.isExpanded(selPath)) {
                        ManagerDatabase.this.tree.collapsePath(selPath);
                    } else {
                        ManagerDatabase.this.tree.expandPath(selPath);
                    }
                }
            }

        });

        this.tree.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_F2) {
                    DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) ManagerDatabase.this.tree.getLastSelectedPathComponent();
                    if (treeNode != null) {
                        AbstractNodeModel nodeModel = (AbstractNodeModel) treeNode.getUserObject();
                        if (nodeModel != null && nodeModel.getPanel() != null && !nodeModel.isRunning()) {
                            nodeModel.getPanel().getLabel().setBackground(HelperUi.COLOR_FOCUS_LOST);
                            nodeModel.getPanel().getLabel().setBorder(HelperUi.BORDER_FOCUS_LOST);
                            nodeModel.setIsEdited(true);
                        }
                    }
                }
            }

        });

        // Action manager for components
        this.tree.setCellEditor(new CellEditorNode());

        // Tree setting
        // allows repaint nodes
        this.tree.setEditable(true);
        this.tree.setShowsRootHandles(true);
        this.tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        // Repaint Gif progressbar
        this.tree.getModel().addTreeModelListener(new TreeModelGifListener());

        this.tree.setBorder(BorderFactory.createEmptyBorder(0, 0, LightScrollPane.THUMB_SIZE, 0));
        LightScrollPane scroller = new LightScrollPane(1, 0, 0, 0, this.tree);

        JMenuBar panelLineBottom = new JMenuBar();
        panelLineBottom.setOpaque(false);
        panelLineBottom.setBorder(null);
        panelLineBottom.setPreferredSize(new Dimension(0, 26));

        panelLineBottom.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, HelperUi.COLOR_COMPONENT_BORDER),
                BorderFactory.createEmptyBorder(1, 0, 1, 1)
            )
        );

        this.add(scroller, BorderLayout.CENTER);
    }
    
    private class TreeModelGifListener implements TreeModelListener {
        
        @Override
        public void treeNodesChanged(TreeModelEvent arg0) {
            if (arg0 != null) {
                ManagerDatabase.this.tree.firePropertyChange(
                    JTree.ROOT_VISIBLE_PROPERTY,
                    !ManagerDatabase.this.tree.isRootVisible(),
                    ManagerDatabase.this.tree.isRootVisible()
                );
                ManagerDatabase.this.tree.treeDidChange();
            }
        }

        @Override
        public void treeStructureChanged(TreeModelEvent arg0) {
            // Do nothing
        }

        @Override
        public void treeNodesRemoved(TreeModelEvent arg0) {
            // Do nothing
        }

        @Override
        public void treeNodesInserted(TreeModelEvent arg0) {
            // Do nothing
        }
        
    }

    // Getter and setter

    public JTree getTree() {
        return this.tree;
    }

}
