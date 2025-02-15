package com.jsql.model.accessible.vendor.h2;

import org.apache.commons.lang3.StringUtils;

public class File {

    private String createTable = StringUtils.EMPTY;
    private String read = StringUtils.EMPTY;

    public String getCreateTable() {
        return this.createTable;
    }

    public void setCreateTable(String createTable) {
        this.createTable = createTable;
    }

    public String getRead() {
        return this.read;
    }

    public void setRead(String read) {
        this.read = read;
    }
}
