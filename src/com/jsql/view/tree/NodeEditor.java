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
package com.jsql.view.tree;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;

import com.jsql.controller.InjectionController;
import com.jsql.model.bean.Column;
import com.jsql.model.bean.Database;
import com.jsql.model.bean.Table;
import com.jsql.view.GUITools;

public class NodeEditor extends AbstractCellEditor implements TreeCellEditor, TreeSelectionListener, MouseListener{
    private static final long serialVersionUID = -190938126492801573L;

    private NodeRenderer treeRenderer;
    private JTree databaseTree;
    private InjectionController controller;
    
    private NodeModel<?> nodeData;

    public NodeEditor(JTree newTree, InjectionController newController, JTabbedPane newTabbedPane) {
        treeRenderer = new NodeRenderer();
        controller = newController;
        newTree.addTreeSelectionListener(this);
        newTree.addMouseListener(this);
        databaseTree = newTree;
    }
    
    @Override
    public Component getTreeCellEditorComponent(JTree tree, Object nodeRenderer,
            boolean selected, boolean expanded, boolean leaf, int row) {
        
        Component componentRenderer = treeRenderer.getTreeCellRendererComponent(tree, nodeRenderer, true, expanded, leaf,
                    row, true);
        
        if (nodeRenderer instanceof DefaultMutableTreeNode) {
            final DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) nodeRenderer;
            Object userObject = currentNode.getUserObject();
            if (userObject instanceof NodeModel<?>) {
                try{
                    nodeData = (NodeModel<?>) userObject;
                    if(componentRenderer instanceof JCheckBox){
                        ((JCheckBox)componentRenderer).addActionListener(new ActionCheckUncheck(nodeData, currentNode));
                    }
                }catch(Exception e){}
            }
        }

        return componentRenderer;
    }
    
    @Override
    public Object getCellEditorValue() {
        return nodeData;
    }

    private class ActionCheckUncheck implements ActionListener{
        NodeModel<?> nodeData;
        DefaultMutableTreeNode currentTableNode;
        public ActionCheckUncheck(NodeModel<?> nodeData, DefaultMutableTreeNode currentTableNode){
            this.nodeData = nodeData;
            this.currentTableNode = currentTableNode;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            JCheckBox columnCheckBox = (JCheckBox) source;
            nodeData.isChecked = columnCheckBox.isSelected();
            
            DefaultTreeModel treeModel = (DefaultTreeModel) databaseTree.getModel();
            DefaultMutableTreeNode tableNode = (DefaultMutableTreeNode) currentTableNode.getParent();
            
            int tableChildCount = treeModel.getChildCount(tableNode);
            boolean isOneChildSelected = false;
            for(int i=0; i < tableChildCount ;i++) {
                DefaultMutableTreeNode currentChild = (DefaultMutableTreeNode) treeModel.getChild(tableNode, i);
                if( currentChild.getUserObject() instanceof NodeModel<?> ){
                    NodeModel<?> columnTreeNodeModel = (NodeModel<?>) currentChild.getUserObject();
                    if(columnTreeNodeModel.isChecked){
                        isOneChildSelected = true;
                        break;
                    }
                }
            }
            
            NodeModel<?> nodeUserObject = (NodeModel<?>) tableNode.getUserObject();
            nodeUserObject.hasChildChecked = isOneChildSelected;
            NodeEditor.this.stopCellEditing(); // !!important!!
        }
    }
    
    private class ActionPauseUnpause implements ActionListener{
        NodeModel<?> nodeData;
        public ActionPauseUnpause(NodeModel<?> nodeData){
            this.nodeData = nodeData;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            if(nodeData.interruptable.isPaused())
                nodeData.interruptable.unPause();
            else
                nodeData.interruptable.pause();
            
            // Restart the action after an unpause
            if(!nodeData.interruptable.isPaused())
                nodeData.interruptable.resume();
            
            NodeEditor.this.stopCellEditing(); // !!important!!
            databaseTree.repaint(); // reload stucked GIF loader
        }
    }
    
    private class ActionLoadStop implements ActionListener{
        NodeModel<?> nodeData;
        DefaultMutableTreeNode currentTableNode;
        
        public ActionLoadStop(NodeModel<?> nodeData, DefaultMutableTreeNode currentTableNode){
            this.nodeData = nodeData;
            this.currentTableNode = currentTableNode;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            DefaultTreeModel treeModel = (DefaultTreeModel) databaseTree.getModel();
            DefaultMutableTreeNode tableNode = currentTableNode;
            List<Column> columnsToSearch = new ArrayList<Column>();
            
            int tableChildCount = treeModel.getChildCount(tableNode);
            for(int i=0; i < tableChildCount ;i++) {
                DefaultMutableTreeNode currentChild = (DefaultMutableTreeNode) treeModel.getChild(tableNode, i);
                if( currentChild.getUserObject() instanceof NodeModel<?> ){
                    NodeModel<?> columnTreeNodeModel = (NodeModel<?>) currentChild.getUserObject();
                    if(columnTreeNodeModel.isChecked){
                        columnsToSearch.add((Column) columnTreeNodeModel.dataObject);
                    }
                }
            }
            
            if(this.nodeData.isTable() && !this.nodeData.isRunning && columnsToSearch.size() == 0)
                return;
            else if(this.nodeData.isDatabase() && !this.nodeData.isRunning && columnsToSearch.size() == 0)
                return;

            if(!this.nodeData.isRunning){
                this.nodeData.interruptable = controller.selectValues(columnsToSearch);
            }else{
                this.nodeData.interruptable.stop();
                this.nodeData.interruptable.unPause();
                this.nodeData.childUpgradeCount = 0;
                this.nodeData.hasIndeterminatedProgress = this.nodeData.hasProgress = false;
                this.nodeData.interruptable.resume();
            }
            this.nodeData.isRunning = !this.nodeData.isRunning;

            NodeEditor.this.stopCellEditing(); // !!important!!
        }
    }
    
    @Override
    public void valueChanged(TreeSelectionEvent arg0) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) databaseTree.getLastSelectedPathComponent();
        if(node == null) return; // get rid of java.lang.NullPointerException

        if(node.getUserObject() instanceof NodeModel){
            NodeModel<?> dataModel = (NodeModel<?>) node.getUserObject();

            if(dataModel.isDatabase()){
                Database selectedDatabase = (Database) dataModel.dataObject;
                if(!dataModel.hasBeenSearched && !dataModel.isRunning){
                    dataModel.interruptable = controller.selectDatabase(selectedDatabase);
                    dataModel.isRunning = true;
                }
            }else if(dataModel.isTable()){
                Table selectedTable = (Table) dataModel.dataObject;
                if(!dataModel.hasBeenSearched && !dataModel.isRunning){
                    dataModel.interruptable = controller.selectTable(selectedTable);
                    dataModel.isRunning = true;
                }
            }
        }
    }
    
    /* compatibility with Windows + Linux */
    public void maybeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()){
            JTree tree = (JTree)e.getSource();
            TreePath path = tree.getPathForLocation(e.getX(), e.getY());
            if (path == null)
                return;

            final DefaultMutableTreeNode currentTableNode = (DefaultMutableTreeNode) path.getLastPathComponent();

            if (currentTableNode.getUserObject() instanceof NodeModel<?>) {
                final NodeModel<?> currentTableModel = (NodeModel<?>) currentTableNode.getUserObject();
                
                if((currentTableModel.isDatabase() && !currentTableModel.hasBeenSearched && currentTableModel.isRunning) ||
                      (currentTableModel.isTable() && (currentTableModel.hasBeenSearched || 
                        !currentTableModel.hasBeenSearched && currentTableModel.isRunning))){
                    JPopupMenu tablePopupMenu = new JPopupMenu();
                    
                    JMenuItem mnCheckAll = new JMenuItem("Check All",'C');
                    JMenuItem mnUncheckAll = new JMenuItem("Uncheck All",'U');
                    JMenuItem mnLoad = new JMenuItem("Load/Stop",'o');
                    JMenuItem mnPause = new JMenuItem("Pause/Resume",'s');
                    mnCheckAll.setIcon(GUITools.EMPTY);
                    mnUncheckAll.setIcon(GUITools.EMPTY);
                    mnLoad.setIcon(GUITools.EMPTY);
                    mnPause.setIcon(GUITools.EMPTY);
                    
                    if(!currentTableModel.hasBeenSearched){
                        mnCheckAll.setEnabled(false);
                        mnUncheckAll.setEnabled(false);
                    }
                    if(!currentTableModel.hasChildChecked && !currentTableModel.isRunning)
                        mnLoad.setEnabled(false);
                    mnLoad.addActionListener(new ActionLoadStop(currentTableModel, currentTableNode));
                    
                    if(!currentTableModel.isRunning)
                        mnPause.setEnabled(false);
                    mnPause.addActionListener(new ActionPauseUnpause(currentTableModel));
                    
                    mnCheckAll.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent arg0) {
                            DefaultTreeModel treeModel = (DefaultTreeModel) databaseTree.getModel();

                            int tableChildCount = treeModel.getChildCount(currentTableNode);
                            for(int i=0; i < tableChildCount ;i++) {
                                DefaultMutableTreeNode currentChild = (DefaultMutableTreeNode) treeModel.getChild(currentTableNode, i);
                                if( currentChild.getUserObject() instanceof NodeModel<?> ){
                                    NodeModel<?> columnTreeNodeModel = (NodeModel<?>) currentChild.getUserObject();
                                    columnTreeNodeModel.isChecked = true;
                                    currentTableModel.hasChildChecked = true;
                                }
                            }

                            treeModel.nodeChanged(currentTableNode);
                        }
                    });
                    
                    mnUncheckAll.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent arg0) {
                            DefaultTreeModel treeModel = (DefaultTreeModel) databaseTree.getModel();
                            
                            int tableChildCount = treeModel.getChildCount(currentTableNode);
                            for(int i=0; i < tableChildCount ;i++) {
                                DefaultMutableTreeNode currentChild = (DefaultMutableTreeNode) treeModel.getChild(currentTableNode, i);
                                if( currentChild.getUserObject() instanceof NodeModel<?> ){
                                    NodeModel<?> columnTreeNodeModel = (NodeModel<?>) currentChild.getUserObject();
                                    columnTreeNodeModel.isChecked = false;
                                    currentTableModel.hasChildChecked = false;
                                }
                            }
                            
                            treeModel.nodeChanged(currentTableNode);
                        }
                    });
                    
                    if(!currentTableModel.isDatabase()){
                        tablePopupMenu.add(mnCheckAll);
                        tablePopupMenu.add(mnUncheckAll);
                        tablePopupMenu.add(new JSeparator());
                    }
                    tablePopupMenu.add(mnLoad);
                    tablePopupMenu.add(mnPause);
                    
                    mnCheckAll.setIcon(GUITools.EMPTY);
                    mnUncheckAll.setIcon(GUITools.EMPTY);
                    mnLoad.setIcon(GUITools.EMPTY);
                    mnPause.setIcon(GUITools.EMPTY);
                    
                    tablePopupMenu.show(tree, e.getX(), e.getY());
                }
            }
        }
    }
    
    @Override public void mousePressed(MouseEvent e) {
        maybeShowPopup(e);
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        maybeShowPopup(e);
    }
    
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}
