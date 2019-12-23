
package com.jsql.model.injection.vendor.model.yaml;


public class Resource {

    private String info;
    private Schema schema;
    private Zipped zipped;
    private Dios dios;
    private File file;

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public Zipped getZipped() {
        return zipped;
    }

    public void setZipped(Zipped zipped) {
        this.zipped = zipped;
    }

    public Dios getDios() {
        return dios;
    }

    public void setDios(Dios dios) {
        this.dios = dios;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

}
