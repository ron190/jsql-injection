package com.jsql.util;

public class UserAgentUtil {
    
    private boolean isCustomUserAgent = false;

    private String customUserAgent = null;
    
    // TODO builder
    public void set(
        boolean isCustomUserAgent,
        String customUserAgent
    ) {
        
        this.isCustomUserAgent = isCustomUserAgent;
        this.customUserAgent = customUserAgent;
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
