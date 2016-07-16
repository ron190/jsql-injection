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

import com.jsql.model.injection.strategy.Strategy;
import com.jsql.view.swing.MediatorGui;

/**
 * Mark the injection as invulnerable to a time based injection.
 */
public class MarkTimebasedStrategy implements InteractionCommand {
    /**
     * @param interactionParams
     */
    public MarkTimebasedStrategy(Object[] interactionParams) {
        // Do nothing
    }

    @Override
    public void execute() {
        MediatorGui.tabManagers().databaseManager.panelStrategy.setEnabled(true);
        MediatorGui.tabManagers().databaseManager.panelStrategy.setText(Strategy.TIME.toString());
        for (int i = 0 ; i < MediatorGui.tabManagers().databaseManager.panelStrategy.getItemCount() ; i++) {
            if (MediatorGui.tabManagers().databaseManager.panelStrategy.getItem(i).getText().equals(Strategy.TIME.toString())) {
                MediatorGui.tabManagers().databaseManager.panelStrategy.getItem(i).setSelected(true);
                break;
            }
        }
    }
}
