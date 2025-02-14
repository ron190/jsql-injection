package com.jsql.model.accessible.vendor.hsqldb;

import org.apache.commons.lang3.StringUtils;

public class File {

    private String createTable = StringUtils.EMPTY;
    private String insertRow = StringUtils.EMPTY;
    private String export = StringUtils.EMPTY;

    public String getCreateTable() {
        return this.createTable;
    }

    public void setCreateTable(String createTable) {
        this.createTable = createTable;
    }

    public String getInsertRow() {
        return this.insertRow;
    }

    public void setInsertRow(String insertRow) {
        this.insertRow = insertRow;
    }

    public String getExport() {
        return this.export;
    }

    public void setExport(String export) {
        this.export = export;
    }
}