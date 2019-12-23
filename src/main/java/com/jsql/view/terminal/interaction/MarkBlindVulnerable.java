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
package com.jsql.view.terminal.interaction;

import org.apache.log4j.Logger;

import com.jsql.view.interaction.InteractionCommand;

/**
 * Mark the injection as vulnerable to a blind injection.
 */
public class MarkBlindVulnerable implements InteractionCommand {
    
    /**
     * Using default log4j.properties from root /
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    /**
     * @param interactionParams
     */
    public MarkBlindVulnerable(Object[] interactionParams) {
        // Do nothing
    }

    @Override
    public void execute() {
        LOGGER.info(InteractionCommand.addGreenColor(this.getClass().getSimpleName()));
    }
    
}
