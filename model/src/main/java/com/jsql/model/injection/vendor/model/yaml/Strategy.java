
package com.jsql.model.injection.vendor.model.yaml;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class Strategy implements Serializable {

    private Configuration configuration = new Configuration();
    private Union union = new Union();
    private String stack = StringUtils.EMPTY;
    private String dns = StringUtils.EMPTY;
    private Binary binary = new Binary();
    private Error error = new Error();

    public Configuration getConfiguration() {
        return this.configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public Union getUnion() {
        return this.union;
    }

    public void setUnion(Union union) {
        this.union = union;
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

    public String getDns() {
        return this.dns;
    }

    public void setDns(String dns) {
        this.dns = dns;
    }
}
