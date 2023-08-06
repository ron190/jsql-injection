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

import java.util.UUID;

/**
 * Append the result of a command in the terminal.
 */
public class AbstractGetShellResult implements InteractionCommand {

    private static final Logger LOGGER = LogManager.getRootLogger();
    
    /**
     * Unique identifier for the terminal. Used to output results of
     * commands in the right shell tab (in case of multiple shell opened).
     */
    private final UUID terminalID;

    /**
     * The result of a command executed in shell.
     */
    private final String result;

    /**
     * @param interactionParams The unique identifier of the terminal and the command's result to display
     */
    public AbstractGetShellResult(Object[] interactionParams) {
        
        this.terminalID = (UUID) interactionParams[0];
        this.result = (String) interactionParams[1];
    }

    @Override
    public void execute() {

        LOGGER.info(() -> AnsiColorUtil.addGreenColor(this.getClass().getSimpleName()));
    }
}
