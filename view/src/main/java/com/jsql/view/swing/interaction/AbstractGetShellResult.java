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
package com.jsql.view.swing.interaction;

import java.util.UUID;

import com.jsql.view.interaction.InteractionCommand;
import com.jsql.view.swing.shell.AbstractShell;
import com.jsql.view.swing.util.MediatorHelper;

/**
 * Append the result of a command in the terminal.
 */
public class AbstractGetShellResult implements InteractionCommand {
    
    /**
     * Unique identifier for the terminal. Used to output results of
     * commands in the right shell tab (in case of multiple shell opened).
     */
    private UUID terminalID;

    /**
     * The result of a command executed in shell.
     */
    private String result;

    /**
     * @param interactionParams The unique identifier of the terminal and the command's result to display
     */
    public AbstractGetShellResult(Object[] interactionParams) {
        
        this.terminalID = (UUID) interactionParams[0];
        this.result = (String) interactionParams[1];
    }

    @Override
    public void execute() {
        
        AbstractShell terminal = MediatorHelper.frame().getConsoles().get(this.terminalID);
        
        terminal.append(this.result);
        
        terminal.append("\n");
        terminal.reset();
    }
}
