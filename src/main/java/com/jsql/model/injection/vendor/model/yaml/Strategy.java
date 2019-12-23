
package com.jsql.model.injection.vendor.model.yaml;


public class Strategy {

    private Configuration configuration;
    private Normal normal;
    private Boolean _boolean;
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
        return _boolean;
    }

    public void setBoolean(Boolean _boolean) {
        this._boolean = _boolean;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

}
