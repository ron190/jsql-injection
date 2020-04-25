package com.jsql.model.injection.vendor.model;

public class Vendor {
    
    private String labelVendor;
    
    private AbstractVendor instanceVendor;
    
    public Vendor(AbstractVendor instanceVendor) {
        
        this.labelVendor = instanceVendor.getModelYaml().getVendor();
        this.instanceVendor = instanceVendor;
    }
    
    public Vendor() {
        
        this.labelVendor = "Database auto";
    }

    public AbstractVendor instance() {
        return this.instanceVendor;
    }
    
    @Override
    public String toString() {
        return this.labelVendor;
    }
    
    public String transformSqlite(String resultToParse) {
        return resultToParse;
    }
}