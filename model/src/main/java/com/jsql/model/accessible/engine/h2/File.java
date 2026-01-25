package com.jsql.model.accessible.engine.h2;

import org.apache.commons.lang3.StringUtils;

public class File {

    private String createTable = StringUtils.EMPTY;
    private String readFromPath = StringUtils.EMPTY;
    private String readFromTempTable = StringUtils.EMPTY;

    public String getCreateTable() {
        return this.createTable;
    }

    public void setCreateTable(String createTable) {
        this.createTable = createTable;
    }

    public String getReadFromPath() {
        return this.readFromPath;
    }

    public void setReadFromPath(String readFromPath) {
        this.readFromPath = readFromPath;
    }

    public String getReadFromTempTable() {
        return this.readFromTempTable;
    }

    public void setReadFromTempTable(String readFromTempTable) {
        this.readFromTempTable = readFromTempTable;
    }
}
