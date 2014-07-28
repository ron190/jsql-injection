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
package com.jsql.view.interaction;

import com.jsql.view.GUIMediator;

/**
 * Append text to the tab Chunk
 */
public class MessageChunk implements IInteractionCommand{
    // Text to append to the Chunk log area
    private String text;

    /**
     * @param interactionParams Text to append
     */
    public MessageChunk(Object[] interactionParams){
        text = (String) interactionParams[0];
    }

    /* (non-Javadoc)
     * @see com.jsql.mvc.view.message.ActionOnView#execute()
     */
    public void execute(){
    	GUIMediator.gui().chunks.append(text);
        GUIMediator.gui().chunks.setCaretPosition(GUIMediator.gui().chunks.getDocument().getLength());
    }
}
