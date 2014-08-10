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

import com.jsql.view.GUIMediator;

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
        if (GUIMediator.model().suspendables.get(this.nodeData.dataObject).isPaused()) {
            GUIMediator.model().suspendables.get(this.nodeData.dataObject).unPause();
        } else {
            GUIMediator.model().suspendables.get(this.nodeData.dataObject).pause();
        }

        // Restart the action after an unpause
        if (!GUIMediator.model().suspendables.get(this.nodeData.dataObject).isPaused()) {
            GUIMediator.model().suspendables.get(this.nodeData.dataObject).resume();
        }

        // !!important!!
        GUIMediator.databaseTree().getCellEditor().stopCellEditing();
        // reload stucked GIF loader
        GUIMediator.databaseTree().repaint();
    }
}