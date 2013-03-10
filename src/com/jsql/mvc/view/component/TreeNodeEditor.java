package com.jsql.mvc.view.component;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractCellEditor;
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

import com.jsql.mvc.controller.InjectionController;
import com.jsql.mvc.model.database.Column;
import com.jsql.mvc.model.database.Database;
import com.jsql.mvc.model.database.Table;



public class TreeNodeEditor extends AbstractCellEditor implements TreeCellEditor, TreeSelectionListener, MouseListener{
    private static final long serialVersionUID = -190938126492801573L;

    private TreeNodeRenderer treeRenderer;
    private JTree databaseTree;
    private InjectionController controller;
    
    private TreeNodeModel<?> nodeData;

    public TreeNodeEditor(JTree newTree, InjectionController newController, JTabbedPane newTabbedPane) {
        treeRenderer = new TreeNodeRenderer();
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
            if (userObject instanceof TreeNodeModel<?>) {
                try{
                    nodeData = (TreeNodeModel<?>) userObject;
                    if(componentRenderer instanceof JCheckBox){
                        ((JCheckBox)componentRenderer).addActionListener(new ActionCheckBox(nodeData, currentNode));
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

    class ActionCheckBox implements ActionListener{
        TreeNodeModel<?> nodeData;
        DefaultMutableTreeNode currentTableNode;
        public ActionCheckBox(TreeNodeModel<?> nodeData, DefaultMutableTreeNode currentTableNode){
            this.nodeData = nodeData;
            this.currentTableNode = currentTableNode;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            JCheckBox columnCheckBox = (JCheckBox) source;
            nodeData.isSelected = columnCheckBox.isSelected();
            
            DefaultTreeModel treeModel = (DefaultTreeModel) databaseTree.getModel();
            DefaultMutableTreeNode tableNode = (DefaultMutableTreeNode) currentTableNode.getParent();
            
            int tableChildCount = treeModel.getChildCount(tableNode);
            boolean isOneChildSelected = false;
            for(int i=0; i < tableChildCount ;i++) {
                DefaultMutableTreeNode currentChild = (DefaultMutableTreeNode) treeModel.getChild(tableNode, i);
                if( currentChild.getUserObject() instanceof TreeNodeModel<?> ){
                    TreeNodeModel<?> columnTreeNodeModel = (TreeNodeModel<?>) currentChild.getUserObject();
                    if(columnTreeNodeModel.isSelected){
                        isOneChildSelected = true;
                        break;
                    }
                }
            }
            
            TreeNodeModel<?> nodeUserObject = (TreeNodeModel<?>) tableNode.getUserObject();
            nodeUserObject.hasChildSelected = isOneChildSelected;
            TreeNodeEditor.this.stopCellEditing(); // !!important!!
        }
    }
    
    class ActionPause implements ActionListener{
        TreeNodeModel<?> nodeData;
        public ActionPause(TreeNodeModel<?> nodeData){
            this.nodeData = nodeData;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            nodeData.interruptable.suspendFlag = !nodeData.interruptable.suspendFlag;
            if(nodeData.interruptable.suspendFlag == false)
                nodeData.interruptable.myresume();
            TreeNodeEditor.this.stopCellEditing(); // !!important!!
            databaseTree.repaint(); // reload stucked GIF loader
        }
    }
    
    class ActionLoad implements ActionListener{
        TreeNodeModel<?> nodeData;
        DefaultMutableTreeNode currentTableNode;
        
        public ActionLoad(TreeNodeModel<?> nodeData, DefaultMutableTreeNode currentTableNode){
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
                if( currentChild.getUserObject() instanceof TreeNodeModel<?> ){
                    TreeNodeModel<?> columnTreeNodeModel = (TreeNodeModel<?>) currentChild.getUserObject();
                    if(columnTreeNodeModel.isSelected){
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
                this.nodeData.interruptable.stopFlag = true;
                this.nodeData.interruptable.suspendFlag = false;
                this.nodeData.childUpgradeCount = 0;
                this.nodeData.hasIndeterminatedProgress = this.nodeData.hasProgress = false;
                this.nodeData.interruptable.myresume();
            }
            this.nodeData.isRunning = !this.nodeData.isRunning;

            TreeNodeEditor.this.stopCellEditing(); // !!important!!
        }
    }
    
    @Override
    public void valueChanged(TreeSelectionEvent arg0) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) databaseTree.getLastSelectedPathComponent();
        if(node == null) return; // get ride of java.lang.NullPointerException

        if(node.getUserObject() instanceof TreeNodeModel){
            TreeNodeModel<?> dataModel = (TreeNodeModel<?>) node.getUserObject();

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

            if (currentTableNode.getUserObject() instanceof TreeNodeModel<?>) {
                final TreeNodeModel<?> currentTableModel = (TreeNodeModel<?>) currentTableNode.getUserObject();
                
                if((currentTableModel.isDatabase() && !currentTableModel.hasBeenSearched && currentTableModel.isRunning) ||
                      (currentTableModel.isTable() && (currentTableModel.hasBeenSearched || 
                        !currentTableModel.hasBeenSearched && currentTableModel.isRunning))){
                    JPopupMenu tablePopupMenu = new JPopupMenu();
                    
                    JMenuItem mnCheckAll = new JMenuItem("Check All");
                    JMenuItem mnUncheckAll = new JMenuItem("Uncheck All");
                    JMenuItem mnLoad = new JMenuItem("Load/Stop");
                    JMenuItem mnPause = new JMenuItem("Pause/Resume");
                    
                    if(!currentTableModel.hasBeenSearched){
                        mnCheckAll.setEnabled(false);
                        mnUncheckAll.setEnabled(false);
                    }
                    if(!currentTableModel.hasChildSelected && !currentTableModel.isRunning)
                        mnLoad.setEnabled(false);
                    mnLoad.addActionListener(new ActionLoad(currentTableModel, currentTableNode));
                    
                    if(!currentTableModel.isRunning)
                        mnPause.setEnabled(false);
                    mnPause.addActionListener(new ActionPause(currentTableModel));
                    
                    mnCheckAll.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent arg0) {
                            DefaultTreeModel treeModel = (DefaultTreeModel) databaseTree.getModel();

                            int tableChildCount = treeModel.getChildCount(currentTableNode);
                            for(int i=0; i < tableChildCount ;i++) {
                                DefaultMutableTreeNode currentChild = (DefaultMutableTreeNode) treeModel.getChild(currentTableNode, i);
                                if( currentChild.getUserObject() instanceof TreeNodeModel<?> ){
                                    TreeNodeModel<?> columnTreeNodeModel = (TreeNodeModel<?>) currentChild.getUserObject();
                                    columnTreeNodeModel.isSelected = true;
                                    currentTableModel.hasChildSelected = true;
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
                                if( currentChild.getUserObject() instanceof TreeNodeModel<?> ){
                                    TreeNodeModel<?> columnTreeNodeModel = (TreeNodeModel<?>) currentChild.getUserObject();
                                    columnTreeNodeModel.isSelected = false;
                                    currentTableModel.hasChildSelected = false;
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