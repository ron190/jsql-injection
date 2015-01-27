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
package com.jsql.view.println.interaction;

import com.jsql.view.swing.HelperGUI;
import com.jsql.view.swing.MediatorGUI;
import com.jsql.view.swing.interaction.IInteractionCommand;

/**
 * Mark the injection as invulnerable to a time based injection.
 */
public class MarkTimebasedInvulnerable implements IInteractionCommand {
    /**
     * @param interactionParams
     */
    public MarkTimebasedInvulnerable(Object[] interactionParams) {
        // Do nothing
    }

    @Override
    public void execute() {
        System.out.println("MarkTimebasedInvulnerable");
    }
}
