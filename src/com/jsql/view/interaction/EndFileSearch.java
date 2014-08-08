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

import com.jsql.view.GUIMediator;

/**
 * End the refreshing of File search button.
 */
public class EndFileSearch implements IInteractionCommand {
    /**
     * @param interactionParams
     */
    public EndFileSearch(Object[] interactionParams) {
        // Do nothing
    }

    public void execute() {
        GUIMediator.left().fileManager.restoreButtonText();
        GUIMediator.left().fileManager.setButtonEnable(true);
        GUIMediator.left().fileManager.hideLoader();
    }
}
