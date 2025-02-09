package com.jsql.model.accessible.vendor.mysql;

import org.apache.commons.lang3.StringUtils;

public class TempTable {

    private String nameDatabase = StringUtils.EMPTY;
    private String drop = StringUtils.EMPTY;
    private String confirm = StringUtils.EMPTY;
    private String create = StringUtils.EMPTY;
    private String insertChunks = StringUtils.EMPTY;
    private String appendChunks = StringUtils.EMPTY;
    private String dump = StringUtils.EMPTY;

    public String getNameDatabase() {
        return this.nameDatabase;
    }

    public void setNameDatabase(String nameDatabase) {
        this.nameDatabase = nameDatabase;
    }

    public String getDrop() {
        return this.drop;
    }

    public void setDrop(String drop) {
        this.drop = drop;
    }

    public String getConfirm() {
        return this.confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }

    public String getCreate() {
        return this.create;
    }

    public void setCreate(String create) {
        this.create = create;
    }

    public String getInsertChunks() {
        return this.insertChunks;
    }

    public void setInsertChunks(String insertChunks) {
        this.insertChunks = insertChunks;
    }

    public String getAppendChunks() {
        return this.appendChunks;
    }

    public void setAppendChunks(String appendChunks) {
        this.appendChunks = appendChunks;
    }

    public String getDump() {
        return this.dump;
    }

    public void setDump(String dump) {
        this.dump = dump;
    }
}