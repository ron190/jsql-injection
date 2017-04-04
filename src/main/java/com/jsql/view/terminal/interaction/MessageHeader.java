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

import com.jsql.model.bean.util.TypeHeader;
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
        url = (String) params.get(TypeHeader.URL);
        post = (String) params.get(TypeHeader.POST);
        header = (String) params.get(TypeHeader.HEADER);
        response = (Map<String, String>) params.get(TypeHeader.RESPONSE);
    }

    @Override
    public void execute() {
        LOGGER.info("Method: " + response.get("Method"));
        LOGGER.info("Url: " + url);
        LOGGER.info("Post: " + post);
        LOGGER.info("Header: " + header);
        LOGGER.info("Content-Length: " + response.get("Content-Length"));
        LOGGER.info("Content-Type: " + response.get("Content-Type"));
        LOGGER.info("\n");
    }
    
}
