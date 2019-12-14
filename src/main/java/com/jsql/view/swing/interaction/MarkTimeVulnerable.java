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

import com.jsql.model.MediatorModel;
import com.jsql.model.injection.strategy.StrategyInjection;
import com.jsql.view.interaction.InteractionCommand;
import com.jsql.view.swing.MediatorGui;

/**
 * Mark the injection as vulnerable to a time based injection.
 */
public class MarkTimeVulnerable implements InteractionCommand {
	
    /**
     * @param nullParam
     */
    public MarkTimeVulnerable(Object[] nullParam) {
        // Do nothing
    }

    @Override
    public void execute() {
        if (MediatorGui.panelAddressBar() == null) {
            LOGGER.error("Unexpected unregistered MediatorGui.panelAddressBar() in "+ this.getClass());
        }
        
        for (int i = 0 ; i < MediatorGui.panelAddressBar().getMenuStrategy().getItemCount() ; i++) {
            if (MediatorGui.panelAddressBar().getMenuStrategy().getItem(i).getText().equals(MediatorModel.model().TIME.toString())) {
                MediatorGui.panelAddressBar().getMenuStrategy().getItem(i).setEnabled(true);
                break;
            }
        }
    }
    
}
