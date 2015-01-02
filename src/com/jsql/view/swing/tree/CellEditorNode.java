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
package com.jsql.view.swing.tree;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import com.jsql.view.swing.MediatorGUI;

/**
 * Tree cell editor responsible for mouse action on nodes.
 */
@SuppressWarnings("serial")
public class CellEditorNode extends AbstractCellEditor implements TreeCellEditor, TreeSelectionListener, MouseListener {
    /**
     * Renderer for nodes included JPanel, button, checkbox, icons...
     */
    private CellRendererNode defaultTreeRenderer;

    /**
     * Value contained in the editor.
     * Returned by getCellEditorValue().
     */
    private AbstractNodeModel nodeData;

    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(CellEditorNode.class);

    /**
     * Build editor, add tree and mouse listener.
     */
    public CellEditorNode() {
        this.defaultTreeRenderer = new CellRendererNode();
        MediatorGUI.databaseTree().addTreeSelectionListener(this);
        MediatorGUI.databaseTree().addMouseListener(this);
    }

    @Override
    public Component getTreeCellEditorComponent(JTree tree, Object nodeRenderer,
            boolean selected, boolean expanded, boolean leaf, int row) {

        Component componentRenderer = defaultTreeRenderer.getTreeCellRendererComponent(tree, nodeRenderer, true, expanded, leaf,
                row, true);

        final DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) nodeRenderer;
        Object userObject = currentNode.getUserObject();
        try {
            this.nodeData = (AbstractNodeModel) userObject;
            if (componentRenderer instanceof JCheckBox) {
                ((JCheckBox) componentRenderer).addActionListener(new ActionCheckUncheck(this.nodeData, currentNode));
            }
        } catch (Exception e) {
            LOGGER.error(e, e);
        }

        return componentRenderer;
    }

    @Override
    public Object getCellEditorValue() {
        return this.nodeData;
    }

    /**
     * Check and unckeck column as checkbox.
     */
    private class ActionCheckUncheck implements ActionListener {

        private AbstractNodeModel nodeData;
        private DefaultMutableTreeNode currentTableNode;

        public ActionCheckUncheck(AbstractNodeModel nodeData, DefaultMutableTreeNode currentTableNode) {
            this.nodeData = nodeData;
            this.currentTableNode = currentTableNode;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            JCheckBox columnCheckBox = (JCheckBox) source;
            this.nodeData.isChecked = columnCheckBox.isSelected();

            DefaultTreeModel treeModel = (DefaultTreeModel) MediatorGUI.databaseTree().getModel();
            DefaultMutableTreeNode tableNode = (DefaultMutableTreeNode) this.currentTableNode.getParent();

            int tableChildCount = treeModel.getChildCount(tableNode);
            boolean isOneChildSelected = false;
            for (int i = 0; i < tableChildCount; i++) {
                DefaultMutableTreeNode currentChild = (DefaultMutableTreeNode) treeModel.getChild(tableNode, i);
                if (currentChild.getUserObject() instanceof AbstractNodeModel) {
                    AbstractNodeModel columnTreeNodeModel = (AbstractNodeModel) currentChild.getUserObject();
                    if (columnTreeNodeModel.isChecked) {
                        isOneChildSelected = true;
                        break;
                    }
                }
            }

            AbstractNodeModel nodeUserObject = (AbstractNodeModel) tableNode.getUserObject();
            nodeUserObject.hasChildChecked = isOneChildSelected;
            // !!important!!
//            NodeEditor.this.stopCellEditing();
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent arg0) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) MediatorGUI.databaseTree().getLastSelectedPathComponent();

        // Get rid of java.lang.NullPointerException
        if (node == null) {
            return;
        }

        if (node.getUserObject() instanceof AbstractNodeModel) {
            AbstractNodeModel dataModel = (AbstractNodeModel) node.getUserObject();
            dataModel.runAction();
        }
    }

    /**
     * Fix compatibility issue with right click on Linux.
     * @param e Mouse event
     */
    private void maybeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            JTree tree = (JTree) e.getSource();
            TreePath path = tree.getPathForLocation(e.getX(), e.getY());
            if (path == null) {
                return;
            }

            DefaultMutableTreeNode currentTableNode = (DefaultMutableTreeNode) path.getLastPathComponent();

            if (currentTableNode.getUserObject() instanceof AbstractNodeModel) {
                AbstractNodeModel currentTableModel = (AbstractNodeModel) currentTableNode.getUserObject();
                if (currentTableModel.verifyShowPopup()) {
                    currentTableModel.showPopup(currentTableNode, path, e.getX(), e.getY());
                }
            }
        }
    }

    @Override 
    public void mousePressed(MouseEvent e) {
        maybeShowPopup(e);
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        maybeShowPopup(e);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Do nothing
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {
        // Do nothing
    }
    
    @Override
    public void mouseExited(MouseEvent e) {
        // Do nothing
    }
}
