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

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.jsql.model.Interruptable;
import com.jsql.model.bean.Column;
import com.jsql.model.bean.Database;
import com.jsql.model.bean.ElementDatabase;
import com.jsql.model.bean.Table;
import com.jsql.view.GUIMediator;
import com.jsql.view.GUITools;
import com.jsql.view.component.RoundBorder;

/**
 * Model adding functional layer to the node, add information to tree node in term of injection process.
 * Used by renderer and editor.
 * @param <T> The database element for this node.
 */
public abstract class NodeModel{
    public ElementDatabase dataObject;
    public String emptyObject;
    
    public int childUpgradeCount = 0;

    public Interruptable interruptable;

    public boolean isChecked = false;
    public boolean isRunning = false;
    public boolean hasChildChecked = false;
    public boolean hasBeenSearched = false;
    public boolean hasIndeterminatedProgress = false;
    public boolean hasProgress = false;

    public NodeModel(ElementDatabase newObject){
        this.dataObject = newObject;
    }

    public NodeModel(String newObject){
    	this.emptyObject = newObject;
    }
    
    public ElementDatabase getParent(){
        return dataObject.getParent();
    }
    public boolean isDatabase(){
        return dataObject instanceof Database;
    }
    public boolean isTable(){
        return dataObject instanceof Table;
    }
    public boolean isColumn(){
        return dataObject instanceof Column;
    }

    public String toString(){
        return dataObject != null ? this.dataObject.getLabel() : emptyObject;
    }
    
    abstract Icon getIcon(boolean leaf);
    abstract void runAction();
    
    public class ActionLoadStop implements ActionListener{
        NodeModel nodeData;
        DefaultMutableTreeNode currentTableNode;
        
        public ActionLoadStop(NodeModel nodeData, DefaultMutableTreeNode currentTableNode){
            this.nodeData = nodeData;
            this.currentTableNode = currentTableNode;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            DefaultTreeModel treeModel = (DefaultTreeModel) GUIMediator.databaseTree().getModel();
            DefaultMutableTreeNode tableNode = currentTableNode;
            List<Column> columnsToSearch = new ArrayList<Column>();
            
            int tableChildCount = treeModel.getChildCount(tableNode);
            for(int i=0; i < tableChildCount ;i++) {
                DefaultMutableTreeNode currentChild = (DefaultMutableTreeNode) treeModel.getChild(tableNode, i);
                if( currentChild.getUserObject() instanceof NodeModel ){
                    NodeModel columnTreeNodeModel = (NodeModel) currentChild.getUserObject();
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
                this.nodeData.interruptable = GUIMediator.controller().selectValues(columnsToSearch);
            }else{
                this.nodeData.interruptable.stop();
                this.nodeData.interruptable.unPause();
                this.nodeData.childUpgradeCount = 0;
                this.nodeData.hasIndeterminatedProgress = this.nodeData.hasProgress = false;
                this.nodeData.interruptable.resume();
            }
            this.nodeData.isRunning = !this.nodeData.isRunning;

            GUIMediator.databaseTree().getCellEditor().stopCellEditing(); // !!important!!
        }
    }
    
    public class ActionPauseUnpause implements ActionListener{
        NodeModel nodeData;
        public ActionPauseUnpause(NodeModel nodeData){
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
            
            GUIMediator.databaseTree().getCellEditor().stopCellEditing(); // !!important!!
            GUIMediator.databaseTree().repaint(); // reload stucked GIF loader
        }
    }
    
    void showPopup(final DefaultMutableTreeNode currentTableNode, TreePath path, int i, int j){
        JPopupMenu tablePopupMenu = new JPopupMenu();
        
        JMenuItem mnLoad = new JMenuItem("Load/Stop",'o');
        JMenuItem mnPause = new JMenuItem("Pause/Resume",'s');
        mnLoad.setIcon(GUITools.EMPTY);
        mnPause.setIcon(GUITools.EMPTY);
        
        if(!this.hasChildChecked && !this.isRunning)
            mnLoad.setEnabled(false);
        mnLoad.addActionListener(new ActionLoadStop(this, currentTableNode));
        
        if(!this.isRunning)
            mnPause.setEnabled(false);
        mnPause.addActionListener(new ActionPauseUnpause(this));
        
        this.displayMenu(tablePopupMenu, path);
        tablePopupMenu.add(mnLoad);
        tablePopupMenu.add(mnPause);
        
        mnLoad.setIcon(GUITools.EMPTY);
        mnPause.setIcon(GUITools.EMPTY);
        
        tablePopupMenu.show(GUIMediator.databaseTree(), i, j);
    }
    
    abstract void displayMenu(JPopupMenu tablePopupMenu, TreePath path);
    
    void displayProgress(NodePanel panel, DefaultMutableTreeNode currentNode){
    	int dataCount = this.dataObject.getCount();
        panel.progressBar.setMaximum(dataCount);
        panel.progressBar.setValue(this.childUpgradeCount);
        panel.progressBar.setVisible(true);

        if(this.interruptable.isPaused()){
            panel.progressBar.pause();
        }
    }
    
    public Component getComponent(JTree tree, Object nodeRenderer,
            boolean selected, boolean expanded, boolean leaf, int row,
            boolean hasFocus){
        
        DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) nodeRenderer;
        NodePanel panel = new NodePanel(tree,currentNode);
    		
    	panel.label.setText(this.toString());
        panel.label.setVisible(true);
        panel.showIcon();

        panel.setIcon(this.getIcon(leaf));

        if(selected){
            panel.label.setBackground(GUITools.SELECTION_BACKGROUND);
        }else{
            panel.label.setBackground(new Color(255,255,255));
            panel.label.setBorder(new RoundBorder(4,1,false));
        }

        if(this.hasProgress){
        	displayProgress(panel, currentNode);
            panel.hideIcon();
        }else if(this.hasIndeterminatedProgress){
            panel.showLoader();
            panel.hideIcon();

            if(this.interruptable.isPaused()){
                ImageIcon animatedGIFPaused = new IconOverlap(GUITools.PATH_PROGRESSBAR, GUITools.PATH_PAUSE);
                animatedGIFPaused.setImageObserver(new AnimatedObserver(GUIMediator.databaseTree(), currentNode));
                panel.setLoaderIcon(animatedGIFPaused);
            }
        }
		return panel;
    }
}
