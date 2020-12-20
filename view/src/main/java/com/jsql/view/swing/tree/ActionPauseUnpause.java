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

import com.jsql.model.suspendable.AbstractSuspendable;
import com.jsql.view.swing.tree.model.AbstractNodeModel;
import com.jsql.view.swing.util.MediatorHelper;

/**
 * Action to pause and unpause injection process.
 */
public class ActionPauseUnpause implements ActionListener {
    
    private AbstractNodeModel nodeModel;

    public ActionPauseUnpause(AbstractNodeModel nodeModel) {
        this.nodeModel = nodeModel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        AbstractSuspendable suspendableTask = MediatorHelper.model().getMediatorUtils().getThreadUtil().get(this.nodeModel.getElementDatabase());
        
        if (suspendableTask == null) {
            return;
        }
        
        if (suspendableTask.isPaused()) {
            
            suspendableTask.unpause();
            
        } else {
            
            suspendableTask.pause();
        }
    }
}