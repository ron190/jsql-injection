package com.jsql.model.accessible.vendor.sqlite;

import org.apache.commons.lang3.StringUtils;

public class Extension {

    private String fileioRead = StringUtils.EMPTY;
    private String fileioWrite = StringUtils.EMPTY;
    private String fileioLs = StringUtils.EMPTY;
    private String exec = StringUtils.EMPTY;

    public String getFileioRead() {
        return this.fileioRead;
    }

    public void setFileioRead(String fileioRead) {
        this.fileioRead = fileioRead;
    }

    public String getFileioWrite() {
        return this.fileioWrite;
    }

    public void setFileioWrite(String fileioWrite) {
        this.fileioWrite = fileioWrite;
    }

    public String getFileioLs() {
        return this.fileioLs;
    }

    public void setFileioLs(String fileioLs) {
        this.fileioLs = fileioLs;
    }

    public String getExec() {
        return this.exec;
    }

    public void setExec(String exec) {
        this.exec = exec;
    }
}
