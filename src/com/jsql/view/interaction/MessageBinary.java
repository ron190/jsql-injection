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

import com.jsql.view.GUIMediator;

/**
 * Append text to the tab Binary.
 */
public class MessageBinary implements IInteractionCommand {
    /**
     * Text to append to the Binary log area.
     */
    private String text;

    /**
     * @param interactionParams Text to append
     */
    public MessageBinary(Object[] interactionParams) {
        text = (String) interactionParams[0];
    }

    public void execute() {
        GUIMediator.bottomPanel().binaryArea.append(text);
        GUIMediator.bottomPanel().binaryArea.setCaretPosition(GUIMediator.bottomPanel().binaryArea.getDocument().getLength());
    }
}
