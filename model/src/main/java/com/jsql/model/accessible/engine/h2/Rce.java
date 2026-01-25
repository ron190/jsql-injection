package com.jsql.model.accessible.engine.h2;

import org.apache.commons.lang3.StringUtils;

public class Rce {

    private String createTable = StringUtils.EMPTY;
    private String callCsvWrite = StringUtils.EMPTY;
    private String scriptSimple = StringUtils.EMPTY;
    private String dropAlias = StringUtils.EMPTY;
    private String createAlias = StringUtils.EMPTY;
    private String runCmd = StringUtils.EMPTY;

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

    public String getDropAlias() {
        return this.dropAlias;
    }

    public void setDropAlias(String dropAlias) {
        this.dropAlias = dropAlias;
    }

    public String getCreateAlias() {
        return this.createAlias;
    }

    public void setCreateAlias(String createAlias) {
        this.createAlias = createAlias;
    }

    public String getRunCmd() {
        return this.runCmd;
    }

    public void setRunCmd(String runCmd) {
        this.runCmd = runCmd;
    }
}
