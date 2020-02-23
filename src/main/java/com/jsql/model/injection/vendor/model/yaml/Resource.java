
package com.jsql.model.injection.vendor.model.yaml;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Resource implements Serializable {

    private String info = "";
    private Schema schema = new Schema();
    private Schema zipped = new Schema();
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

    public Schema getZipped() {
        return this.zipped;
    }

    public void setZipped(Schema zipped) {
        this.zipped = zipped;
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
