package com.jsql.model.accessible.vendor.hsqldb;

import org.apache.commons.lang3.StringUtils;

public class Read {

    private String createTable = StringUtils.EMPTY;
    private String result = StringUtils.EMPTY;
    private String performImport = StringUtils.EMPTY;

    public String getCreateTable() {
        return this.createTable;
    }

    public void setCreateTable(String createTable) {
        this.createTable = createTable;
    }

    public String getResult() {
        return this.result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getPerformImport() {
        return this.performImport;
    }

    public void setPerformImport(String performImport) {
        this.performImport = performImport;
    }
}
