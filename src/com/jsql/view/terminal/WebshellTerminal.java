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

public class WebshellTerminal extends Terminal {
    private static final long serialVersionUID = -7387042122390007426L;

    public WebshellTerminal(InjectionModel model, UUID terminalID, String wbhPath) {
        super(model, terminalID, wbhPath, "terminal");
    }

    @Override
    void action(String cmd, UUID terminalID, String wbhPath, String... arg) {
        this.model.rao.executeShell(cmd, terminalID, wbhPath);
    }

}
