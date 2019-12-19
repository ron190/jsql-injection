package com.jsql.model.injection.vendor.model;

public class Vendor {
    
    private final String labelVendor;
    
    private final AbstractVendor instanceVendor;
    
    public Vendor(String labelVendor, AbstractVendor instanceVendor) {
        this.labelVendor = labelVendor;
        this.instanceVendor = instanceVendor;
    }
    
    public AbstractVendor instance() {
        return this.instanceVendor;
    }
    
    @Override
    public String toString() {
        return this.labelVendor;
    }
    
    // TODO SQLITE
    public String transformSQLite(String resultToParse) {
        return resultToParse;
    }
    
}