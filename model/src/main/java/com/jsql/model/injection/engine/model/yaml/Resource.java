
package com.jsql.model.injection.engine.model.yaml;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class Resource implements Serializable {

    private String info = StringUtils.EMPTY;
    private String exploit = StringUtils.EMPTY;
    private Schema schema = new Schema();
    private Schema zip = new Schema();
    private Schema dios = new Schema();

    public String getInfo() {
        return this.info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Schema getSchema() {
        return this.schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public Schema getZip() {
        return this.zip;
    }

    public void setZip(Schema zip) {
        this.zip = zip;
    }

    public Schema getDios() {
        return this.dios;
    }

    public void setDios(Schema dios) {
        this.dios = dios;
    }

    public String getExploit() {
        return this.exploit;
    }

    public void setExploit(String exploit) {
        this.exploit = exploit;
    }
}
