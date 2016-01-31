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
package com.jsql.view.scan.interaction;

import org.apache.log4j.Logger;

/**
 * Mark the injection as invulnerable to a error based injection.
 */
public class MarkErrorbasedStrategy implements IInteractionCommand {
    private static final Logger LOGGER = Logger.getLogger(MarkErrorbasedStrategy.class);

    /**
     * @param interactionParams
     */
    public MarkErrorbasedStrategy(Object[] interactionParams) {
        // Do nothing
    }

    @Override
    public void execute() {
        LOGGER.trace("Using Error based injection.");
    }
}
