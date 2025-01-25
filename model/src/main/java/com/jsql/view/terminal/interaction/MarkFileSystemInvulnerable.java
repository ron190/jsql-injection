/*******************************************************************************
 * Copyhacked (H) 2012-2025.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.terminal.interaction;

import com.jsql.util.AnsiColorUtil;
import com.jsql.view.interaction.InteractionCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Mark the injection as invulnerable to a union injection.
 */
public class MarkFileSystemInvulnerable implements InteractionCommand {

    private static final Logger LOGGER = LogManager.getRootLogger();

    public MarkFileSystemInvulnerable(Object[] interactionParams) {
        // Do nothing
    }

    @Override
    public void execute() {
        LOGGER.debug(() -> AnsiColorUtil.addRedColor(this.getClass().getSimpleName()));
    }
}
