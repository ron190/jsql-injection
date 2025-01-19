/*******************************************************************************
 * Copyhacked (H) 2012-2025.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.terminal.interaction;

import com.jsql.model.bean.util.Header;
import com.jsql.util.AnsiColorUtil;
import com.jsql.view.interaction.InteractionCommand;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.Map;

public class MessageHeader implements InteractionCommand {
    
    private static final Logger LOGGER = LogManager.getRootLogger();

    private final String url;
    private final String post;
    private final Map<String, String> header;
    private final Map<String, String> response;
    private final String source;

    @SuppressWarnings("unchecked")
    public MessageHeader(Object[] interactionParams) {
        Map<Header, Object> params = (Map<Header, Object>) interactionParams[0];
        this.url = (String) params.getOrDefault(Header.URL, StringUtils.EMPTY);
        this.post = (String) params.getOrDefault(Header.POST, StringUtils.EMPTY);
        this.header = (Map<String, String>) params.getOrDefault(Header.HEADER, Collections.emptyMap());
        this.response = (Map<String, String>) params.getOrDefault(Header.RESPONSE, Collections.emptyMap());
        this.source = (String) params.getOrDefault(Header.SOURCE, StringUtils.EMPTY);
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
