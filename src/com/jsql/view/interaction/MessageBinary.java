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
 * Append text to the tab Binary
 */
public class MessageBinary implements Interaction{
    // The main View
    private GUI gui;

    // Text to append to the Binary log area
    private String text;

    /**
     * @param mainGUI
     * @param interactionParams Text to append
     */
    public MessageBinary(GUI mainGUI, Object[] interactionParams){
        gui = mainGUI;

        text = (String) interactionParams[0];
    }

    /* (non-Javadoc)
     * @see com.jsql.mvc.view.message.ActionOnView#execute()
     */
    public void execute(){
        gui.binaryArea.append(text);
        gui.binaryArea.setCaretPosition(gui.binaryArea.getDocument().getLength());
    }
}
