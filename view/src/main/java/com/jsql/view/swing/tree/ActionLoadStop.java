/*******************************************************************************
 * Copyhacked (H) 2012-2020.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 *******************************************************************************/
package com.jsql.view.swing.tree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.jsql.model.bean.database.Column;
import com.jsql.model.suspendable.AbstractSuspendable;
import com.jsql.view.swing.tree.model.AbstractNodeModel;
import com.jsql.view.swing.util.MediatorHelper;

/**
 * Action to start and stop injection process.
 */
public class ActionLoadStop implements ActionListener {
    
    private AbstractNodeModel nodeModel;
    private DefaultMutableTreeNode currentTableNode;

    public ActionLoadStop(AbstractNodeModel nodeModel, DefaultMutableTreeNode currentTableNode) {
        
        this.nodeModel = nodeModel;
        this.currentTableNode = currentTableNode;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        final List<Column> columnsToSearch = this.getSelectedColumns();

        if (!this.nodeModel.isRunning() && columnsToSearch.isEmpty()) {
            
            return;
        }

        if (!this.nodeModel.isRunning()) {
            
            this.startListValues(columnsToSearch);
            
        } else {
            
            this.stopAbstractNode();
        }
        
        this.nodeModel.setRunning(!this.nodeModel.isRunning());
    }

    private void startListValues(final List<Column> columnsToSearch) {
        
        new SwingWorker<>() {
            
            @Override
            protected Object doInBackground() throws Exception {
                
                Thread.currentThread().setName("SwingWorkerActionLoadStop");
                MediatorHelper.model().getDataAccess().listValues(columnsToSearch);
                
                return null;
            }
        }.execute();
    }

    private void stopAbstractNode() {
        
        AbstractSuspendable suspendableTask = MediatorHelper.model().getMediatorUtils().getThreadUtil().get(this.nodeModel.getElementDatabase());
        
        // Fix #21394: NullPointerException on stop()
        if (suspendableTask != null) {
            suspendableTask.stop();
        }
        
        this.nodeModel.setIndexProgress(0);
        this.nodeModel.setProgressing(false);
        this.nodeModel.setLoading(false);
        
        MediatorHelper.model().getMediatorUtils().getThreadUtil().remove(this.nodeModel.getElementDatabase());
    }

    private List<Column> getSelectedColumns() {
        
        DefaultTreeModel treeModel = (DefaultTreeModel) MediatorHelper.treeDatabase().getModel();
        DefaultMutableTreeNode tableNode = this.currentTableNode;
        final List<Column> columnsToSearch = new ArrayList<>();

        int tableChildCount = treeModel.getChildCount(tableNode);
        for (var i = 0 ; i < tableChildCount ; i++) {
            
            DefaultMutableTreeNode currentChild = (DefaultMutableTreeNode) treeModel.getChild(tableNode, i);
            
            if (currentChild.getUserObject() instanceof AbstractNodeModel) {
                
                AbstractNodeModel columnTreeNodeModel = (AbstractNodeModel) currentChild.getUserObject();
                if (columnTreeNodeModel.isSelected()) {
                    
                    columnsToSearch.add((Column) columnTreeNodeModel.getElementDatabase());
                }
            }
        }
        
        return columnsToSearch;
    }
}