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
package com.jsql.view.swing.shell;

import java.net.MalformedURLException;
import java.util.UUID;

import com.jsql.view.swing.util.MediatorHelper;

/**
 * A terminal for web shell injection.
 */
@SuppressWarnings("serial")
public class ShellWeb extends AbstractShell {
    
    /**
     * Build a webshell instance.
     * @param terminalID Unique identifier to discriminate beyond multiple opened terminals
     * @param urlShell URL of current shell
     * @throws MalformedURLException
     */
    public ShellWeb(UUID terminalID, String urlShell) throws MalformedURLException {
        
        super(terminalID, urlShell, "system");
        
        this.setName("webShell");
    }

    @Override
    public void action(String command, UUID terminalID, String urlShell, String... arg) {
        
        MediatorHelper.model().getResourceAccess().runWebShell(command, terminalID, urlShell);
    }
}
