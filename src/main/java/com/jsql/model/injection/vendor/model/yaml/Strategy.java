
package com.jsql.model.injection.vendor.model.yaml;


public class Strategy {

    private Configuration configuration;
    private Normal normal;
    private Boolean booleanStrategy;
    private Error error;

    public Configuration getConfiguration() {
        return this.configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public Normal getNormal() {
        return this.normal;
    }

    public void setNormal(Normal normal) {
        this.normal = normal;
    }

    public Boolean getBoolean() {
        return this.booleanStrategy;
    }

    public void setBoolean(Boolean booleanStrategy) {
        this.booleanStrategy = booleanStrategy;
    }

    public Error getError() {
        return this.error;
    }

    public void setError(Error error) {
        this.error = error;
    }

}
