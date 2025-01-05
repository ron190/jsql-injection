package com.jsql.util;

public class UserAgentUtil {
    
    private String customUserAgent = null;

    public void withCustomUserAgent(String customUserAgent) {
        this.customUserAgent = customUserAgent;
    }
    
    
    // Getter and setter

    public String getCustomUserAgent() {
        return this.customUserAgent;
    }

    public void setCustomUserAgent(String customUserAgent) {
        this.customUserAgent = customUserAgent;
    }
}
