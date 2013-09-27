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

import com.jsql.view.GUI;

/**
 * Append a text to the tab Header
 */
public class MessageHeader implements Interaction{
    // The main View
    private GUI gui;

    // The text to append to the tab
    private String text;

    /**
     * @param mainGUI
     * @param interactionParams Text to append
     */
    public MessageHeader(GUI mainGUI, Object[] interactionParams){
        gui = mainGUI;

        text = (String) interactionParams[0];
    }

    /* (non-Javadoc)
     * @see com.jsql.mvc.view.message.ActionOnView#execute()
     */
    public void execute(){
        gui.headers.append(text);
        gui.headers.setCaretPosition(gui.headers.getDocument().getLength());
    }
}
