/*******************************************************************************
 * Copyhacked (H) 2012-2025.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.interaction;

import com.jsql.view.interaction.InteractionCommand;
import com.jsql.view.swing.util.MediatorHelper;

/**
 * End the refreshing of the main Start injection button.
 */
public class EndPreparation implements InteractionCommand {
    
    public EndPreparation(Object[] interactionParams) {
        // Do nothing
    }

    @Override
    public void execute() {
        MediatorHelper.panelAddressBar().getPanelTrailingAddress().endPreparation();
        if (MediatorHelper.model().shouldErasePreviousInjection()) {
            MediatorHelper.tabManagersCards().endPreparation();
        }
    }
}
