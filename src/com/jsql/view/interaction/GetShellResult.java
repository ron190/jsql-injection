/*******************************************************************************
 * Copyhacked (H) 2012-2013.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.interaction;

import java.util.UUID;

import com.jsql.view.GUIMediator;
import com.jsql.view.terminal.Terminal;

/**
 * Append the result of a command in the terminal
 */
public class GetShellResult implements InteractionCommand{
    // Unique identifier for the terminal.
    // Used for outputing results of commands in the right shell tab (in case of multiple shell opened)
    private UUID terminalID;

    // The result of a command executed in shell
    private String result;

    // The command executed in shell
    private String cmd;

    /**
     * @param mainGUI
     * @param interactionParams The unique identifier of the terminal and the command's result to display
     */
    public GetShellResult(Object[] interactionParams){
        terminalID = (UUID) interactionParams[0];
        result = (String) interactionParams[1];
        cmd = (String) interactionParams[2];
    }

    /* (non-Javadoc)
     * @see com.jsql.mvc.view.message.ActionOnView#execute()
     */
    public void execute(){
        Terminal terminal = GUIMediator.gui().consoles.get(terminalID);
        
        if(!result.equals(""))
            terminal.append(result);
        else
//            terminal.append("No result\n");
	        terminal.append("No result.\nTry "+cmd.trim()+" 2>&1 to get error messages.\n");
        
        terminal.append("\n");
        terminal.reset();
    }
}
