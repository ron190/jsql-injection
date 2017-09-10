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
 * Append text to the tab Chunk.
 */
public class MessageChunk implements InteractionCommand {
    
    /**
     * Text to append to the Chunk log area.
     */
    private String text;

    /**
     * @param interactionParams Text to append
     */
    public MessageChunk(Object[] interactionParams) {
        this.text = (String) interactionParams[0];
    }

    @Override
    public void execute() {
        if (MediatorGui.panelConsoles() == null) {
            LOGGER.error("Unexpected unregistered MediatorGui.panelConsoles() in "+ this.getClass());
        }
        
        try {
            MediatorGui.panelConsoles().getChunkTab().append(this.text +"\n");
            MediatorGui.panelConsoles().getChunkTab().setCaretPosition(MediatorGui.panelConsoles().getChunkTab().getDocument().getLength());
            
            int tabIndex = MediatorGui.tabConsoles().indexOfTab("Chunk");
            if (0 <= tabIndex && tabIndex < MediatorGui.tabConsoles().getTabCount()) {
                Component tabHeader = MediatorGui.tabConsoles().getTabComponentAt(tabIndex);
                if (MediatorGui.tabConsoles().getSelectedIndex() != tabIndex) {
                    tabHeader.setFont(tabHeader.getFont().deriveFont(Font.BOLD));
                }
            }
        } catch(ArrayIndexOutOfBoundsException e) {
            // Fix #4770 on chunkTab.append()
            LOGGER.error(e.getMessage(), e);
        }
    }
    
}
