package com.jsql.model.bean;

import java.util.Map;

public class HTTPHeader {
    public String url;
    public String cookie;
    public String post;
    public String header;
    public Map<String, String> response;
    
    public HTTPHeader(String url, String cookie, String post, String header,
            Map<String, String> response) {
        this.url = url;
        this.cookie = cookie;
        this.post = post;
        this.header = header;
        this.response = response;
    }
    
    @Override
    public String toString() {
        return url;
    }
}
