
package com.jsql.model.injection.vendor.model.yaml;

public class ModelYaml {

    private String vendor = "";
    private Resource resource = new Resource();
    private Strategy strategy = new Strategy();

    public String getVendor() {
        return this.vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public Resource getResource() {
        return this.resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public Strategy getStrategy() {
        return this.strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

}
