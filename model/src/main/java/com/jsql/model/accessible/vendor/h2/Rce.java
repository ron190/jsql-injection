package com.jsql.model.accessible.vendor.h2;

import org.apache.commons.lang3.StringUtils;

public class Rce {

    private String createTable = StringUtils.EMPTY;
    private String callCsvWrite = StringUtils.EMPTY;
    private String scriptSimple = StringUtils.EMPTY;

    public String getCreateTable() {
        return this.createTable;
    }

    public void setCreateTable(String createTable) {
        this.createTable = createTable;
    }

    public String getCallCsvWrite() {
        return this.callCsvWrite;
    }

    public void setCallCsvWrite(String callCsvWrite) {
        this.callCsvWrite = callCsvWrite;
    }

    public String getScriptSimple() {
        return this.scriptSimple;
    }

    public void setScriptSimple(String scriptSimple) {
        this.scriptSimple = scriptSimple;
    }
}
