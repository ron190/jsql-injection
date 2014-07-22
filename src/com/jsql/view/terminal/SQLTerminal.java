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
package com.jsql.view.terminal;

import java.util.UUID;

import com.jsql.model.InjectionModel;

@SuppressWarnings("serial")
public class SQLTerminal extends Terminal {

	public SQLTerminal(InjectionModel model, UUID terminalID, String wbhPath, String... args) {
        super(model, terminalID, wbhPath, "sql");
        this.args = args; 
    }

    @Override
    void action(String cmd, UUID terminalID, String wbhPath, String... arg) {
        this.model.rao.executeSQLShell(cmd, terminalID, wbhPath, arg[0], arg[1]);
    }

}
