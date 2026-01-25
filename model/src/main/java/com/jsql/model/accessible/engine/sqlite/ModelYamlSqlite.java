
package com.jsql.model.accessible.engine.sqlite;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class ModelYamlSqlite implements Serializable {

    private Extension extension = new Extension();
    private String writeFile = StringUtils.EMPTY;
    private String udf = StringUtils.EMPTY;

    public String getWriteFile() {
        return this.writeFile;
    }

    public void setWriteFile(String writeFile) {
        this.writeFile = writeFile;
    }

    public String getUdf() {
        return this.udf;
    }

    public void setUdf(String udf) {
        this.udf = udf;
    }

    public Extension getExtension() {
        return this.extension;
    }

    public void setExtension(Extension extension) {
        this.extension = extension;
    }
}