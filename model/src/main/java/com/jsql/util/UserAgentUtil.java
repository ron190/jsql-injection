package com.jsql.util;

import org.apache.log4j.Logger;

public class UserAgentUtil {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
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
