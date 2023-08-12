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

import com.jsql.util.AnsiColorUtil;
import com.jsql.view.interaction.InteractionCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Append the result of a command in the terminal.
 */
public class AbstractGetShellResult implements InteractionCommand {

    private static final Logger LOGGER = LogManager.getRootLogger();
    
    /**
     * @param interactionParams The unique identifier of the terminal and the command's result to display
     */
    public AbstractGetShellResult(Object[] interactionParams) {
        // nothing
    }

    @Override
    public void execute() {

        LOGGER.info(() -> AnsiColorUtil.addGreenColor(this.getClass().getSimpleName()));
    }
}
