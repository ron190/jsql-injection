package com.jsql.view.scan;
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
 * View in the MVC pattern, defines all the components
 * and process actions sent by the model.<br>
 * Main groups of components:<br>
 * - at the top: textfields input,<br>
 * - at the center: tree on the left, table on the right,<br>
 * - at the bottom: information labels.
 */
public class ScanListTerminal extends SubscriberInteraction {
    
    public ScanListTerminal() {
        
        super("com.jsql.view.scan.interaction");
    }
}
