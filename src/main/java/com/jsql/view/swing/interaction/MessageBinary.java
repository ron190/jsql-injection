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
package com.jsql.view.swing.interaction;

import java.awt.Component;
import java.awt.Font;

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
        if (MediatorGui.panelConsoles() == null) {
            LOGGER.error("Unexpected unregistered MediatorGui.panelConsoles() in "+ this.getClass());
        }
        
        MediatorGui.panelConsoles().getBinaryTab().append("\t"+ this.text);
        MediatorGui.panelConsoles().getBinaryTab().setCaretPosition(MediatorGui.panelConsoles().getBinaryTab().getDocument().getLength());
        
        int tabIndex = MediatorGui.tabConsoles().indexOfTab("Boolean");
        if (0 <= tabIndex && tabIndex < MediatorGui.tabConsoles().getTabCount()) {
            Component tabHeader = MediatorGui.tabConsoles().getTabComponentAt(tabIndex);
            if (MediatorGui.tabConsoles().getSelectedIndex() != tabIndex) {
                tabHeader.setFont(tabHeader.getFont().deriveFont(Font.BOLD));
            }
        }
    }
    
}
