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
 * End the refreshing of the main Start injection button.
 */
public class EndPreparation implements InteractionCommand {
    
    /**
     * @param interactionParams
     */
    public EndPreparation(Object[] interactionParams) {
        // Do nothing
    }

    @Override
    public void execute() {
        
        MediatorGui.panelAddressBar().getAddressMenuBar().endPreparation();

        if (MediatorModel.model().shouldErasePreviousInjection()) {
            
            MediatorGui.tabManagers().endPreparation();
        }
    }
}
