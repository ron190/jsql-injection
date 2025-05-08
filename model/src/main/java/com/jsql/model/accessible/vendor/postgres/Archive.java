package com.jsql.model.accessible.vendor.postgres;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class Archive implements Serializable {

    private String getStatus = StringUtils.EMPTY;
    private String getPathConf = StringUtils.EMPTY;
    private String getConfLength = StringUtils.EMPTY;
    private String putCmd = StringUtils.EMPTY;
    private String reloadConf = StringUtils.EMPTY;
    private String getCmd = StringUtils.EMPTY;
    private String runWal = StringUtils.EMPTY;

    public String getGetPathConf() {
        return this.getPathConf;
    }

    public void setGetPathConf(String getPathConf) {
        this.getPathConf = getPathConf;
    }

    public String getGetStatus() {
        return this.getStatus;
    }

    public void setGetStatus(String getStatus) {
        this.getStatus = getStatus;
    }

    public String getGetConfLength() {
        return this.getConfLength;
    }

    public void setGetConfLength(String getConfLength) {
        this.getConfLength = getConfLength;
    }

    public String getPutCmd() {
        return this.putCmd;
    }

    public void setPutCmd(String putCmd) {
        this.putCmd = putCmd;
    }

    public String getReloadConf() {
        return this.reloadConf;
    }

    public void setReloadConf(String reloadConf) {
        this.reloadConf = reloadConf;
    }

    public String getGetCmd() {
        return this.getCmd;
    }

    public void setGetCmd(String getCmd) {
        this.getCmd = getCmd;
    }

    public String getRunWal() {
        return this.runWal;
    }

    public void setRunWal(String runWal) {
        this.runWal = runWal;
    }
}