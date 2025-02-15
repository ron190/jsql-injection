
package com.jsql.model.accessible.vendor.sqlite;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class ModelYamlSqlite implements Serializable {

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
}