
package com.jsql.model.injection.vendor.model.yaml;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class Strategy implements Serializable {

    private Configuration configuration = new Configuration();
    private Normal normal = new Normal();
    private String stack = StringUtils.EMPTY;
    private Binary binary = new Binary();
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

    public Binary getBinary() {
        return this.binary;
    }

    public void setBinary(Binary binary) {
        this.binary = binary;
    }

    public Error getError() {
        return this.error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public String getStack() {
        return this.stack;
    }

    public void setStack(String stack) {
        this.stack = stack;
    }
}
