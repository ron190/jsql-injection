package com.jsql.model.tamper;

public abstract class IEncloseSelect {
    String lead;
    String trail;
    
    public String getLead() {
        return lead;
    }
    public void setLead(String lead) {
        this.lead = lead;
    }
    public String getTrail() {
        return trail;
    }
    public void setTrail(String trail) {
        this.trail = trail;
    }
}
