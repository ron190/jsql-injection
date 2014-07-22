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
 * Append text to the tab Console
 */
public class MessageConsole implements InteractionCommand{
    // Text to append to the Console log area
    private String text;

    /**
     * @param interactionParams Text to append
     */
    public MessageConsole(Object[] interactionParams){
        text = (String) interactionParams[0];
    }

    /* (non-Javadoc)
     * @see com.jsql.mvc.view.message.ActionOnView#execute()
     */
    public void execute(){
    	GUIMediator.gui().consoleArea.append(text);
        GUIMediator.gui().consoleArea.setCaretPosition(GUIMediator.gui().consoleArea.getDocument().getLength());
    }
}
