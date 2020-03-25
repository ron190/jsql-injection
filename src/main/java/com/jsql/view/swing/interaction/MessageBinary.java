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
import com.jsql.view.swing.MediatorGui;

/**
 * Append text to the tab Binary.
 */
public class MessageBinary implements InteractionCommand {
    
    /**
     * Text to append to the Binary log area.
     */
    private String text;

    /**
     * @param interactionParams Text to append
     */
    public MessageBinary(Object[] interactionParams) {
        
        this.text = (String) interactionParams[0];
    }

    @Override
    public void execute() {
        
        MediatorGui.panelConsoles().messageBinary(this.text);
        
        MediatorGui.tabConsoles().highlightTab("Boolean");
    }
}
