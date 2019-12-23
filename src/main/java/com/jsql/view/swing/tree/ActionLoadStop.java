/*******************************************************************************
 * Copyhacked (H) 2012-2016.
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

import com.jsql.model.MediatorModel;
import com.jsql.model.bean.database.Column;
import com.jsql.model.suspendable.AbstractSuspendable;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.tree.model.AbstractNodeModel;
import com.jsql.view.swing.tree.model.AbstractNodeModel.JPopupMenu2;

/**
 * Action to start and stop injection process.
 */
public class ActionLoadStop implements ActionListener {
    
    AbstractNodeModel nodeModel;
    
    DefaultMutableTreeNode currentTableNode;
    
    JPopupMenu2 popupMenu;

    public ActionLoadStop(AbstractNodeModel nodeModel, DefaultMutableTreeNode currentTableNode, JPopupMenu2 popupMenu) {
        this.nodeModel = nodeModel;
        this.currentTableNode = currentTableNode;
        this.popupMenu = popupMenu;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DefaultTreeModel treeModel = (DefaultTreeModel) MediatorGui.treeDatabase().getModel();
        DefaultMutableTreeNode tableNode = this.currentTableNode;
        final List<Column> columnsToSearch = new ArrayList<>();

        int tableChildCount = treeModel.getChildCount(tableNode);
        for (int i = 0 ; i < tableChildCount ; i++) {
            DefaultMutableTreeNode currentChild = (DefaultMutableTreeNode) treeModel.getChild(tableNode, i);
            if (currentChild.getUserObject() instanceof AbstractNodeModel) {
                AbstractNodeModel columnTreeNodeModel = (AbstractNodeModel) currentChild.getUserObject();
                if (columnTreeNodeModel.isSelected()) {
                    columnsToSearch.add((Column) columnTreeNodeModel.getElementDatabase());
                }
            }
        }

        if (!this.nodeModel.isRunning() && columnsToSearch.isEmpty()) {
            return;
        }

        if (!this.nodeModel.isRunning()) {
            new SwingWorker<Object, Object>(){
                @Override
                protected Object doInBackground() throws Exception {
                    Thread.currentThread().setName("SwingWorkerActionLoadStop");
                    MediatorModel.model().getDataAccess().listValues(columnsToSearch);
                    return null;
                }
            }.execute();
        } else {
            AbstractSuspendable<?> suspendableTask = MediatorModel.model().getMediatorUtils().getThreadUtil().get(this.nodeModel.getElementDatabase());
            
            // Fix #21394: NullPointerException on stop()
            if (suspendableTask != null) {
                suspendableTask.stop();
            }
            
            this.nodeModel.setIndexProgress(0);
            this.nodeModel.setProgressing(false);
            this.nodeModel.setLoading(false);
            
            MediatorModel.model().getMediatorUtils().getThreadUtil().remove(this.nodeModel.getElementDatabase());
        }
        this.nodeModel.setRunning(!this.nodeModel.isRunning());
    }
    
}