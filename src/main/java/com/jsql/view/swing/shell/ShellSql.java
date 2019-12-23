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
package com.jsql.view.swing.shell;

import java.net.MalformedURLException;
import java.util.UUID;

import com.jsql.model.MediatorModel;

/**
 * A terminal for SQL shell injection.
 */
@SuppressWarnings("serial")
public class ShellSql extends AbstractShell {
    
   /**
     * Build a SQL shell instance.
     * @param terminalID Unique identifier to discriminate beyond multiple opened terminals
     * @param urlShell URL of current shell
     * @param args User and password
     * @throws MalformedURLException
     */
    public ShellSql(UUID terminalID, String urlShell, String... args) throws MalformedURLException {
        super(terminalID, urlShell, "sql");
        this.loginPassword = args;
    }

    @Override
    void action(String cmd, UUID terminalID, String wbhPath, String... arg) {
        MediatorModel.model().getResourceAccess().runSqlShell(cmd, terminalID, wbhPath, arg[0], arg[1]);
    }
    
}
