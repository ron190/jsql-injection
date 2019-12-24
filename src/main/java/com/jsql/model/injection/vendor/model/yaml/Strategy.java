
package com.jsql.model.injection.vendor.model.yaml;


public class Strategy {

    private Configuration configuration;
    private Normal normal;
    private Boolean booleanStrategy;
    private Error error;

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public Normal getNormal() {
        return normal;
    }

    public void setNormal(Normal normal) {
        this.normal = normal;
    }

    public Boolean getBoolean() {
        return booleanStrategy;
    }

    public void setBoolean(Boolean booleanStrategy) {
        this.booleanStrategy = booleanStrategy;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

}
