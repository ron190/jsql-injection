package com.jsql.util;

import org.apache.commons.lang3.StringUtils;

public class UserAgentUtil {
    
    private String customUserAgent = StringUtils.EMPTY;

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
