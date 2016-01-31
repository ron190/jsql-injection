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
package com.jsql.view.junit.interaction;

import org.apache.log4j.Logger;

import com.jsql.view.swing.interaction.IInteractionCommand;

/**
 * Mark the injection as invulnerable to a blind injection.
 */
public class MarkBlindInvulnerable implements IInteractionCommand {
    /**
     * Using default log4j.properties from root /
     */
    private static final Logger LOGGER = Logger.getLogger(MarkBlindInvulnerable.class);

    /**
     * @param interactionParams
     */
    public MarkBlindInvulnerable(Object[] interactionParams) {
        // Do nothing
    }

    @Override
    public void execute() {
        LOGGER.info("MarkBlindInvulnerable");
        LOGGER.info("\n");
    }
}
