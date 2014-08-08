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
import com.jsql.view.GUITools;

/**
 * Mark the injection as vulnerable to a error-based injection.
 */
public class MarkErrorbasedVulnerable implements IInteractionCommand {
    /**
     * @param interactionParams
     */
    public MarkErrorbasedVulnerable(Object[] interactionParams) {
        // Do nothing
    }

    public void execute() {
        GUIMediator.status().setErrorBasedIcon(GUITools.TICK);
    }
}
