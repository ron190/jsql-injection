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
package com.jsql.view.interaction;

import com.jsql.view.MediatorGUI;

/**
 * End the refreshing of administration page search button.
 */
public class EndAdminSearch implements IInteractionCommand {
    /**
     * @param interactionParams
     */
    public EndAdminSearch(Object[] interactionParams) {
        // Do nothing
    }

    @Override
    public void execute() {
        MediatorGUI.left().adminPageManager.restoreButtonText();
        MediatorGUI.left().adminPageManager.setButtonEnable(true);
        MediatorGUI.left().adminPageManager.hideLoader();
    }
}
