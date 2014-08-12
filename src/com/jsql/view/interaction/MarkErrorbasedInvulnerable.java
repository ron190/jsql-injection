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
import com.jsql.view.ToolsGUI;

/**
 * Mark the injection as invulnerable to a error based injection.
 */
public class MarkErrorbasedInvulnerable implements IInteractionCommand {
    /**
     * @param interactionParams
     */
    public MarkErrorbasedInvulnerable(Object[] interactionParams) {
        // Do nothing
    }

    @Override
    public void execute() {
        MediatorGUI.status().setErrorBasedIcon(ToolsGUI.SQUARE_RED);
    }
}
