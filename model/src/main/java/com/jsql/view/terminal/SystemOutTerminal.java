package com.jsql.view.terminal;
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

import com.jsql.view.interaction.SubscriberInteraction;

/**
 * View in the MVC pattern for integration test, process actions sent by the model.<br>
 */
public class SystemOutTerminal extends SubscriberInteraction {

    public SystemOutTerminal() {
        
        super("com.jsql.view.terminal.interaction");
    }
}
