
package com.jsql.model.accessible.vendor.postgres;

import java.io.Serializable;

public class ModelYamlPostgres implements Serializable {

    private Udf udf = new Udf();
    private File file = new File();

    public Udf getUdf() {
        return this.udf;
    }

    public void setUdf(Udf udf) {
        this.udf = udf;
    }

    public File getFile() {
        return this.file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}