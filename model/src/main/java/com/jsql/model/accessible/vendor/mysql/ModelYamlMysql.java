
package com.jsql.model.accessible.vendor.mysql;

import java.io.Serializable;

public class ModelYamlMysql implements Serializable {

    private File file = new File();
    private Udf udf = new Udf();

    public File getFile() {
        return this.file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Udf getUdf() {
        return this.udf;
    }

    public void setUdf(Udf udf) {
        this.udf = udf;
    }
}


