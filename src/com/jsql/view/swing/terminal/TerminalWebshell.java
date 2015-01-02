/*******************************************************************************
 * Copyhacked (H) 2012-2014.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.terminal;

import java.util.UUID;

import com.jsql.model.injection.InjectionModel;
import com.jsql.model.injection.MediatorModel;

/**
 * A terminal for web shell injection.
 */
@SuppressWarnings("serial")
public class TerminalWebshell extends AbstractTerminal {
    /**
     * Build a webshell instance.
     * @param terminalID Unique identifier to discriminate beyond multiple opened terminals
     * @param shellURL URL of current shell
     */
    public TerminalWebshell(UUID terminalID, String shellURL) {
        super(terminalID, shellURL, "terminal");
    }

    @Override
    void action(String cmd, UUID terminalID, String shellURL, String... arg) {
        MediatorModel.model().ressourceAccessObject.executeShell(cmd, terminalID, shellURL);
    }
}
