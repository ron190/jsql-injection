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

import com.jsql.view.interaction.InteractionCommand;
import com.jsql.view.swing.util.MediatorHelper;

/**
 * Create a new tab for the terminal.
 */
public class CreateSQLShellTab extends CreateTabHelper implements InteractionCommand {
    
    /**
     * Full path of the shell file on remote host.
     */
    private String path;

    // Url of the shell webpage on remote host
    private String url;
    
    private String user;
    
    private String pass;

    /**
     * @param interactionParams The local path and url for the shell
     */
    public CreateSQLShellTab(Object[] interactionParams) {
        
        this.path = (String) interactionParams[0];
        this.url = (String) interactionParams[1];
        this.user = (String) interactionParams[2];
        this.pass = (String) interactionParams[3];
    }

    @Override
    public void execute() {
        
        MediatorHelper.tabResults().createSQLShellTab(this.url, this.user, this.pass, this.path);
    }
}
