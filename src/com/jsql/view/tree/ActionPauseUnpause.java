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
package com.jsql.view.tree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.tree.DefaultMutableTreeNode;

import com.jsql.view.MediatorGUI;

/**
 * Action to pause and unpause injection process.
 */
public class ActionPauseUnpause implements ActionListener {
    AbstractNodeModel nodeData;
    DefaultMutableTreeNode currentTableNode;

    public ActionPauseUnpause(AbstractNodeModel nodeData, DefaultMutableTreeNode currentTableNode) {
        this.nodeData = nodeData;
        this.currentTableNode = currentTableNode;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (MediatorGUI.model().suspendables.get(this.nodeData.dataObject).isPaused()) {
            MediatorGUI.model().suspendables.get(this.nodeData.dataObject).unPause();
        } else {
            MediatorGUI.model().suspendables.get(this.nodeData.dataObject).pause();
        }

        // Restart the action after an unpause
        if (!MediatorGUI.model().suspendables.get(this.nodeData.dataObject).isPaused()) {
            MediatorGUI.model().suspendables.get(this.nodeData.dataObject).resume();
        }

        // !!important!!
        MediatorGUI.databaseTree().getCellEditor().stopCellEditing();
        // reload stucked GIF loader
        MediatorGUI.databaseTree().repaint();
    }
}