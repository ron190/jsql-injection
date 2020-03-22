/*******************************************************************************
 * Copyhacked (H) 2012-2020.
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
import com.jsql.view.interaction.InteractionCommand;
import com.jsql.view.swing.MediatorGui;

/**
 * Mark the injection as invulnerable to a blind injection.
 */
public class MarkBlindStrategy implements InteractionCommand {
    
    /**
     * @param interactionParams
     */
    public MarkBlindStrategy(Object[] interactionParams) {
        // Do nothing
    }

    @Override
    public void execute() {
        if (MediatorGui.panelAddressBar() == null) {
            LOGGER.error("Unexpected unregistered MediatorGui.managerDatabase() in "+ this.getClass());
        }
        
        MediatorGui.panelAddressBar().getAddressMenuBar().getMenuStrategy().setText(MediatorModel.model().getMediatorStrategy().getBlind().toString());
        for (int i = 0 ; i < MediatorGui.panelAddressBar().getAddressMenuBar().getMenuStrategy().getItemCount() ; i++) {
            if (MediatorGui.panelAddressBar().getAddressMenuBar().getMenuStrategy().getItem(i).getText().equals(MediatorModel.model().getMediatorStrategy().getBlind().toString())) {
                MediatorGui.panelAddressBar().getAddressMenuBar().getMenuStrategy().getItem(i).setSelected(true);
                break;
            }
        }
    }
    
}
