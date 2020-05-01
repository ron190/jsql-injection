
package com.jsql.model.injection.vendor.model.yaml;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Strategy implements Serializable {

    private Configuration configuration = new Configuration();
    private Normal normal = new Normal();
    private Boolean booleanStrategy = new Boolean();
    private Error error = new Error();

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
