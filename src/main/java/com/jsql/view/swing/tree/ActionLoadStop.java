/*******************************************************************************
 * Copyhacked (H) 2012-2014.
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

import com.jsql.model.accessible.DataAccess;
import com.jsql.model.bean.Column;
import com.jsql.model.injection.MediatorModel;
import com.jsql.model.injection.suspendable.AbstractSuspendable;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.tree.model.AbstractNodeModel;

/**
 * Action to start and stop injection process.
 */
public class ActionLoadStop implements ActionListener {
    AbstractNodeModel nodeData;
    DefaultMutableTreeNode currentTableNode;

    public ActionLoadStop(AbstractNodeModel nodeData, DefaultMutableTreeNode currentTableNode) {
        this.nodeData = nodeData;
        this.currentTableNode = currentTableNode;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DefaultTreeModel treeModel = (DefaultTreeModel) MediatorGui.treeDatabase().getModel();
        DefaultMutableTreeNode tableNode = currentTableNode;
        final List<Column> columnsToSearch = new ArrayList<>();

        int tableChildCount = treeModel.getChildCount(tableNode);
        for (int i = 0; i < tableChildCount; i++) {
            DefaultMutableTreeNode currentChild = (DefaultMutableTreeNode) treeModel.getChild(tableNode, i);
            if (currentChild.getUserObject() instanceof AbstractNodeModel) {
                AbstractNodeModel columnTreeNodeModel = (AbstractNodeModel) currentChild.getUserObject();
                if (columnTreeNodeModel.isChecked) {
                    columnsToSearch.add((Column) columnTreeNodeModel.dataObject);
                }
            }
        }

        if (!this.nodeData.isRunning && columnsToSearch.isEmpty()) {
            return;
        }

        if (!this.nodeData.isRunning) {
            new SwingWorker<Object, Object>(){
                @Override
                protected Object doInBackground() throws Exception {
                    DataAccess.listValues(columnsToSearch);
                    return null;
                }
            }.execute();
        } else {
            AbstractSuspendable suspendableTask = MediatorModel.model().suspendables.get(this.nodeData.dataObject);
            
            suspendableTask.stop();
            suspendableTask.unpause();
            this.nodeData.childUpgradeCount = 0;
            this.nodeData.hasIndeterminatedProgress = false;
            this.nodeData.hasProgress = false;
            suspendableTask.resume();
            
            MediatorModel.model().suspendables.remove(this.nodeData.dataObject);
        }
        this.nodeData.isRunning = !this.nodeData.isRunning;
    }
}