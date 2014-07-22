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
 * Append a text to the tab Header
 */
public class MessageHeader implements InteractionCommand{
    // The text to append to the tab
    private String text;

    /**
     * @param mainGUI
     * @param interactionParams Text to append
     */
    public MessageHeader(Object[] interactionParams){
        text = (String) interactionParams[0];
    }

    /* (non-Javadoc)
     * @see com.jsql.mvc.view.message.ActionOnView#execute()
     */
    public void execute(){
    	GUIMediator.gui().headers.append(text);
        GUIMediator.gui().headers.setCaretPosition(GUIMediator.gui().headers.getDocument().getLength());
    }
}
