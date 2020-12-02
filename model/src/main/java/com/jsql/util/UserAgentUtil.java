package com.jsql.util;

public class UserAgentUtil {
    
    private boolean isCustomUserAgent = false;

    private String customUserAgent = null;

    public UserAgentUtil withCustomUserAgent(String customUserAgent) {
        this.isCustomUserAgent = true;
        this.customUserAgent = customUserAgent;
        return this;
    }
    
    
    // Getter and setter

    public String getCustomUserAgent() {
        return this.customUserAgent;
    }

    public void setCustomUserAgent(String customUserAgent) {
        this.customUserAgent = customUserAgent;
    }

    public boolean isCustomUserAgent() {
        return this.isCustomUserAgent;
    }
    
    public void setIsCustomUserAgent(boolean isCustomUserAgent) {
        this.isCustomUserAgent = isCustomUserAgent;
    }
}
