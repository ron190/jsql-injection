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

import com.jsql.view.interaction.InteractionCommand;

/**
 * Mark the injection as vulnerable to a basic injection.
 */
public class MarkNormalVulnerable implements InteractionCommand {
    
    /**
     * @param nullParam
     */
    public MarkNormalVulnerable(Object[] nullParam) {
        // Do nothing
    }

    @Override
    public void execute() {
        LOGGER.info(InteractionCommand.addGreenColor(this.getClass().getSimpleName()));
    }
    
}
