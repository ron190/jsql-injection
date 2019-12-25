
package com.jsql.model.injection.vendor.model.yaml;


public class Resource {

    private String info;
    private Schema schema;
    private Zipped zipped;
    private Dios dios;
    private File file;

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

    public Zipped getZipped() {
        return this.zipped;
    }

    public void setZipped(Zipped zipped) {
        this.zipped = zipped;
    }

    public Dios getDios() {
        return this.dios;
    }

    public void setDios(Dios dios) {
        this.dios = dios;
    }

    public File getFile() {
        return this.file;
    }

    public void setFile(File file) {
        this.file = file;
    }

}
