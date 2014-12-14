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
 * Erase the screen.
 */
public class ResetInterface implements IInteractionCommand {
    /**
     * @param interactionParams
     */
    public ResetInterface(Object[] interactionParams) {
        // Do nothing
    }

    @Override
    public void execute() {
        MediatorGUI.gui().resetInterface();
    }
}
