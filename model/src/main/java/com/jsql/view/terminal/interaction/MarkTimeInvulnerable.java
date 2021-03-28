/*******************************************************************************
 * Copyhacked (H) 2012-2020.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.terminal.interaction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.util.AnsiColorUtil;
import com.jsql.view.interaction.InteractionCommand;

/**
 * Mark the injection as invulnerable to a time based injection.
 */
public class MarkTimeInvulnerable implements InteractionCommand {

    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * @param interactionParams
     */
    public MarkTimeInvulnerable(Object[] interactionParams) {
        // Do nothing
    }

    @Override
    public void execute() {

        LOGGER.debug(() -> AnsiColorUtil.addRedColor(this.getClass().getSimpleName()));
    }
}
