/*******************************************************************************
 * Copyhacked (H) 2012-2016.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.interaction;

import com.jsql.model.injection.strategy.StrategyInjection;
import com.jsql.view.interaction.InteractionCommand;
import com.jsql.view.swing.MediatorGui;

/**
 * Mark the injection as invulnerable to a blind injection.
 */
public class MarkBlindInvulnerable implements InteractionCommand {
	
    /**
     * @param interactionParams
     */
    public MarkBlindInvulnerable(Object[] interactionParams) {
        // Do nothing
    }

    @Override
    public void execute() {
        for (int i = 0 ; i < MediatorGui.managerDatabase().getMenuStrategy().getItemCount() ; i++) {
            if (MediatorGui.managerDatabase().getMenuStrategy().getItem(i).getText().equals(StrategyInjection.BLIND.toString())) {
                MediatorGui.managerDatabase().getMenuStrategy().getItem(i).setEnabled(false);
                break;
            }
        }
    }
    
}
