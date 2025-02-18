package com.jsql.model.accessible.vendor.postgres;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class Sql implements Serializable {

    private String dropTable = StringUtils.EMPTY;
    private String createTable = StringUtils.EMPTY;
    private Confirm confirm = new Confirm();
    private String resultCmd = StringUtils.EMPTY;
    private String runCmd = StringUtils.EMPTY;
    private String clean = StringUtils.EMPTY;
    private String runFunc = StringUtils.EMPTY;

    public String getDropTable() {
        return this.dropTable;
    }

    public void setDropTable(String dropTable) {
        this.dropTable = dropTable;
    }

    public String getCreateTable() {
        return this.createTable;
    }

    public void setCreateTable(String createTable) {
        this.createTable = createTable;
    }

    public Confirm getConfirm() {
        return this.confirm;
    }

    public void setConfirm(Confirm confirm) {
        this.confirm = confirm;
    }

    public String getResultCmd() {
        return this.resultCmd;
    }

    public void setResultCmd(String resultCmd) {
        this.resultCmd = resultCmd;
    }

    public String getRunCmd() {
        return this.runCmd;
    }

    public void setRunCmd(String runCmd) {
        this.runCmd = runCmd;
    }

    public String getClean() {
        return this.clean;
    }

    public void setClean(String clean) {
        this.clean = clean;
    }

    public String getRunFunc() {
        return this.runFunc;
    }

    public void setRunFunc(String runFunc) {
        this.runFunc = runFunc;
    }
}