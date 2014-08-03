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
package com.jsql.view.tree;

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

import com.jsql.model.InjectionModel;
import com.jsql.view.GUIMediator;

/**
 * Tree cell editor responsible for mouse action on nodes.
 */
@SuppressWarnings("serial")
public class NodeEditor extends AbstractCellEditor implements TreeCellEditor, TreeSelectionListener, MouseListener{

    private NodeRenderer treeRenderer;

    private NodeModel nodeData;

    public NodeEditor() {
        treeRenderer = new NodeRenderer();
        GUIMediator.databaseTree().addTreeSelectionListener(this);
        GUIMediator.databaseTree().addMouseListener(this);
    }

    @Override
    public Component getTreeCellEditorComponent(JTree tree, Object nodeRenderer,
            boolean selected, boolean expanded, boolean leaf, int row) {

        Component componentRenderer = treeRenderer.getTreeCellRendererComponent(tree, nodeRenderer, true, expanded, leaf,
                row, true);

        final DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) nodeRenderer;
        Object userObject = currentNode.getUserObject();
        try{
            nodeData = (NodeModel) userObject;
            if(componentRenderer instanceof JCheckBox){
                ((JCheckBox)componentRenderer).addActionListener(new ActionCheckUncheck(nodeData, currentNode));
            }
        }catch(Exception e){
            InjectionModel.logger.error(e, e);
        }

        return componentRenderer;
    }

    @Override
    public Object getCellEditorValue() {
        return nodeData;
    }

    /**
     * Check and unckeck column as checkbox.
     */
    private class ActionCheckUncheck implements ActionListener{
        
        NodeModel nodeData;
        DefaultMutableTreeNode currentTableNode;
        
        public ActionCheckUncheck(NodeModel nodeData, DefaultMutableTreeNode currentTableNode){
            this.nodeData = nodeData;
            this.currentTableNode = currentTableNode;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            JCheckBox columnCheckBox = (JCheckBox) source;
            nodeData.isChecked = columnCheckBox.isSelected();

            DefaultTreeModel treeModel = (DefaultTreeModel) GUIMediator.databaseTree().getModel();
            DefaultMutableTreeNode tableNode = (DefaultMutableTreeNode) currentTableNode.getParent();

            int tableChildCount = treeModel.getChildCount(tableNode);
            boolean isOneChildSelected = false;
            for(int i=0; i < tableChildCount ;i++) {
                DefaultMutableTreeNode currentChild = (DefaultMutableTreeNode) treeModel.getChild(tableNode, i);
                if( currentChild.getUserObject() instanceof NodeModel ){
                    NodeModel columnTreeNodeModel = (NodeModel) currentChild.getUserObject();
                    if(columnTreeNodeModel.isChecked){
                        isOneChildSelected = true;
                        break;
                    }
                }
            }

            NodeModel nodeUserObject = (NodeModel) tableNode.getUserObject();
            nodeUserObject.hasChildChecked = isOneChildSelected;
            // !!important!!
//            NodeEditor.this.stopCellEditing(); 
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent arg0) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) GUIMediator.databaseTree().getLastSelectedPathComponent();
        
        // Get rid of java.lang.NullPointerException
        if(node == null) {
            return;
        }

        if(node.getUserObject() instanceof NodeModel){
            NodeModel dataModel = (NodeModel) node.getUserObject();
            dataModel.runAction();
        }
    }

    /**
     * Fix compatibility issue with right click on Linux.
     * @param e Mouse event
     */
    public void maybeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()){
            JTree tree = (JTree)e.getSource();
            TreePath path = tree.getPathForLocation(e.getX(), e.getY());
            if (path == null){
                return;
            }

            DefaultMutableTreeNode currentTableNode = (DefaultMutableTreeNode) path.getLastPathComponent();

            if (currentTableNode.getUserObject() instanceof NodeModel) {
                NodeModel currentTableModel = (NodeModel) currentTableNode.getUserObject();
                if(currentTableModel.verifyShowPopup()){
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
