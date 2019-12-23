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

import com.jsql.view.interaction.InteractionCommand;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.manager.util.StateButton;

/**
 * End the refreshing of File search button.
 */
public class EndFileSearch implements InteractionCommand {
    
    /**
     * @param interactionParams
     */
    public EndFileSearch(Object[] interactionParams) {
        // Do nothing
    }

    @Override
    public void execute() {
        if (MediatorGui.managerFile() == null) {
            LOGGER.error("Unexpected unregistered MediatorGui.managerFile() in "+ this.getClass());
        }
        
        MediatorGui.managerFile().restoreButtonText();
        MediatorGui.managerFile().setButtonEnable(true);
        MediatorGui.managerFile().hideLoader();
        MediatorGui.managerFile().setStateButton(StateButton.STARTABLE);
    }
    
}
