package com.jsql.model.bean.util;

import java.util.Map;

/**
 * An HTTP object containing request and response data.
 */
public class HttpHeader {
    
    /**
     * GET request.
     */
    private String url;
    
    /**
     * POST request.
     */
    private String post;
    
    /**
     * Header request.
     */
    private Map<String, String> header;
    
    /**
     * Header sent back by server.
     */
    private Map<String, String> response;
    
    private String source;
    
    /**
     * Create object containing HTTP data to display in Network panel.
     * @param url URL called
     * @param post POST text sent with url
     * @param header HEADER text sent with url
     * @param response RESPONSE header sent by url
     */
    public HttpHeader(
        String url,
        String post,
        Map<String, String> header,
        Map<String, String> response,
        String source
    ) {
        
        this.url = url;
        this.post = post;
        this.header = header;
        this.response = response;
        this.source = source;
    }

    @Override
    public String toString() {
        return this.url;
    }
    
    // Getter and setter
    
    public String getUrl() {
        return this.url;
    }

    public String getPost() {
        return this.post;
    }

    public Map<String, String> getHeader() {
        return this.header;
    }

    public Map<String, String> getResponse() {
        return this.response;
    }

    public String getSource() {
        return this.source;
    }
}
