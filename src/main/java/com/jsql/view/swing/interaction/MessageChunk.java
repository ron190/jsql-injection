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

import org.apache.log4j.Logger;

import com.jsql.view.swing.MediatorGui;

/**
 * Append text to the tab Chunk.
 */
public class MessageChunk implements InteractionCommand {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(MessageChunk.class);
    
    /**
     * Text to append to the Chunk log area.
     */
    private String text;

    /**
     * @param interactionParams Text to append
     */
    public MessageChunk(Object[] interactionParams) {
        text = (String) interactionParams[0];
    }

    @Override
    public void execute() {
        try {
            MediatorGui.panelConsoles().chunkTab.append(text);
            MediatorGui.panelConsoles().chunkTab.setCaretPosition(MediatorGui.panelConsoles().chunkTab.getDocument().getLength());
            
            int tabIndex = MediatorGui.tabConsoles().indexOfTab("Chunk");
            if (0 <= tabIndex && tabIndex < MediatorGui.tabConsoles().getTabCount()) {
                Component tabHeader = MediatorGui.tabConsoles().getTabComponentAt(tabIndex);
                if (MediatorGui.tabConsoles().getSelectedIndex() != tabIndex) {
                    tabHeader.setFont(tabHeader.getFont().deriveFont(Font.BOLD));
                }
            }            
        } catch(ArrayIndexOutOfBoundsException e) {
            // Fix #4770 on chunkTab.append()
            LOGGER.error(e, e);
        }
    }
}
