
package com.jsql.model.injection.vendor.model.yaml;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("serial")
public class Resource implements Serializable {

    private String info = StringUtils.EMPTY;
    private Schema schema = new Schema();
    private Schema zip = new Schema();
    private Schema dios = new Schema();
    private File file = new File();

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

    public File getFile() {
        return this.file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
