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

import java.util.Map;

import org.apache.log4j.Logger;

import com.jsql.model.bean.util.Header;
import com.jsql.view.interaction.InteractionCommand;

/**
 * Append a text to the tab Header.
 */
public class MessageHeader implements InteractionCommand {
    
    /**
     * Using default log4j.properties from root /
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    // The text to append to the tab
    private String url;
    
    private String post;
    
    private String header;
    
    private Map<String, String> response;

    /**
     * @param interactionParams Text to append
     */
    @SuppressWarnings("unchecked")
    public MessageHeader(Object[] interactionParams) {
        Map<String, Object> params = (Map<String, Object>) interactionParams[0];
        this.url = (String) params.get(Header.URL);
        this.post = (String) params.get(Header.POST);
        this.header = (String) params.get(Header.HEADER);
        this.response = (Map<String, String>) params.get(Header.RESPONSE);
    }

    @Override
    public void execute() {
        LOGGER.debug(InteractionCommand.addGreenColor(this.getClass().getSimpleName()));
        LOGGER.debug("Method: " + this.response.get("Method"));
        LOGGER.debug("Url: " + this.url);
        LOGGER.debug("Post: " + this.post);
        LOGGER.debug("Header: " + this.header);
        LOGGER.debug("Content-Length: " + this.response.get("Content-Length"));
        LOGGER.debug("Content-Type: " + this.response.get("Content-Type"));
    }
    
}
