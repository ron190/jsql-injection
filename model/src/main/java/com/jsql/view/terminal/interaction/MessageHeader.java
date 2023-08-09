/*******************************************************************************
 * Copyhacked (H) 2012-2020.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.terminal.interaction;

import com.jsql.model.bean.util.Header;
import com.jsql.util.AnsiColorUtil;
import com.jsql.view.interaction.InteractionCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * Append a text to the tab Header.
 */
public class MessageHeader implements InteractionCommand {
    
    private static final Logger LOGGER = LogManager.getRootLogger();

    // The text to append to the tab
    private final String url;
    private final String post;
    private final Map<String, String> header;
    private final Map<String, String> response;
    private final String source;

    /**
     * @param interactionParams Text to append
     */
    @SuppressWarnings("unchecked")
    public MessageHeader(Object[] interactionParams) {
        
        Map<Header, Object> params = (Map<Header, Object>) interactionParams[0];
        this.url = (String) params.get(Header.URL);
        this.post = (String) params.get(Header.POST);
        this.header = (Map<String, String>) params.get(Header.HEADER);
        this.response = (Map<String, String>) params.get(Header.RESPONSE);
        this.source = (String) params.get(Header.SOURCE);
    }

    @Override
    public void execute() {
        
        LOGGER.debug(() -> AnsiColorUtil.addGreenColor(this.getClass().getSimpleName()));
        
        LOGGER.debug("Method: {}", () -> this.response.get("Method"));
        LOGGER.debug("Url: {}", this.url);
        LOGGER.debug("Post: {}", this.post);
        LOGGER.debug("Header: {}", this.header);
        LOGGER.debug("Content-Length: {}", () -> this.response.get("Content-Length"));
        LOGGER.debug("Content-Type: {}", () -> this.response.get("Content-Type"));
        LOGGER.debug("Source: {}", () -> this.source);
    }
}
