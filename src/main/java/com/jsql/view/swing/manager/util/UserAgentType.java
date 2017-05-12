package com.jsql.view.swing.manager.util;

public enum UserAgentType {
    
    BROWSER("Browsers"),
    OFFLINE_BROWSER("Offline browsers"),
    MOBILE_BROWSER("Mobile browsers"),
    EMAIL_CLIENT("Email clients"),
    LIBRARY("Library"),
    WAP_BROWSER("WAP browsers"),
    VALIDATOR("Validators"),
    FEED_READER("Feed readers"),
    MULTIMEDIA_PLAYER("Multimedia Players"),
    OTHER("Others");
    
    private String label;
    
    private UserAgentType(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return this.label;
    }
    
}