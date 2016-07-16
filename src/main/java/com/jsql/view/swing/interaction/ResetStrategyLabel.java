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
package com.jsql.view.swing.interaction;

import com.jsql.view.swing.MediatorGui;

/**
 * Erase the screen.
 */
public class ResetStrategyLabel implements InteractionCommand {
    /**
     * @param interactionParams
     */
    public ResetStrategyLabel(Object[] interactionParams) {
        // Do nothing
    }

    @Override
    public void execute() {
        for (int i = 0 ; i < MediatorGui.tabManagers().databaseManager.panelStrategy.getItemCount() ; i++) {
            MediatorGui.tabManagers().databaseManager.panelStrategy.getItem(i).setEnabled(false);
            MediatorGui.tabManagers().databaseManager.panelStrategy.getItem(i).setSelected(false);
        }
    }
}
