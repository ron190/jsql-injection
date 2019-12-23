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
package com.jsql.view.terminal.interaction;

import org.apache.log4j.Logger;

import com.jsql.view.interaction.InteractionCommand;

/**
 * Append text to the tab Binary.
 */
public class MessageBinary implements InteractionCommand {
    
    /**
     * Using default log4j.properties from root /
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    /**
     * Text to append to the Binary log area.
     */
    private String text;

    /**
     * @param interactionParams Text to append
     */
    public MessageBinary(Object[] interactionParams) {
        this.text = (String) interactionParams[0];
    }

    @Override
    public void execute() {
        LOGGER.info(this.text);
    }
    
}
