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
 * Mark the injection as invulnerable to a normal injection.
 */
public class MarkNormalStrategy implements InteractionCommand {
	
    /**
     * @param interactionParams
     */
    public MarkNormalStrategy(Object[] interactionParams) {
        // Do nothing
    }

    @Override
    public void execute() {
        if (MediatorGui.managerDatabase() == null) {
            LOGGER.error("Unexpected unregistered MediatorGui.managerDatabase() in "+ this.getClass());
        }
        
        MediatorGui.managerDatabase().getMenuStrategy().setText(StrategyInjection.NORMAL.toString());
        for (int i = 0 ; i < MediatorGui.managerDatabase().getMenuStrategy().getItemCount() ; i++) {
            if (MediatorGui.managerDatabase().getMenuStrategy().getItem(i).getText().equals(StrategyInjection.NORMAL.toString())) {
                MediatorGui.managerDatabase().getMenuStrategy().getItem(i).setSelected(true);
                break;
            }
        }
    }
    
}
