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
package com.jsql.view.swing.interaction;

import java.awt.Component;
import java.awt.Font;

import com.jsql.view.swing.MediatorGUI;

/**
 * Append text to the tab Chunk.
 */
public class MessageChunk implements IInteractionCommand {
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
        MediatorGUI.bottomPanel().chunkTab.append(text);
        MediatorGUI.bottomPanel().chunkTab.setCaretPosition(MediatorGUI.bottomPanel().chunkTab.getDocument().getLength());
        
        int tabIndex = MediatorGUI.bottom().indexOfTab("Chunk");
        if (0 <= tabIndex && tabIndex < MediatorGUI.bottom().getTabCount()) {
            Component tabHeader = MediatorGUI.bottom().getTabComponentAt(tabIndex);
            if (MediatorGUI.bottom().getSelectedIndex() != tabIndex) {
                tabHeader.setFont(tabHeader.getFont().deriveFont(Font.BOLD));
            }
        }
    }
}
