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
package com.jsql.view.interaction;

import com.jsql.view.MediatorGUI;

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
        MediatorGUI.bottomPanel().chunks.append(text);
        MediatorGUI.bottomPanel().chunks.setCaretPosition(MediatorGUI.bottomPanel().chunks.getDocument().getLength());
    }
}
