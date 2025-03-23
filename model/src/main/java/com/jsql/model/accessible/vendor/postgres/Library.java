package com.jsql.model.accessible.vendor.postgres;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class Library implements Serializable {

    private String loFromText = StringUtils.EMPTY;
    private String loToFile = StringUtils.EMPTY;
    private String dropFunc = StringUtils.EMPTY;
    private String createFunction = StringUtils.EMPTY;
    private String runFunc = StringUtils.EMPTY;

    public String getDropFunc() {
        return this.dropFunc;
    }

    public void setDropFunc(String dropFunc) {
        this.dropFunc = dropFunc;
    }

    public String getCreateFunction() {
        return this.createFunction;
    }

    public void setCreateFunction(String createFunction) {
        this.createFunction = createFunction;
    }

    public String getRunFunc() {
        return this.runFunc;
    }

    public void setRunFunc(String runFunc) {
        this.runFunc = runFunc;
    }

    public String getLoFromText() {
        return this.loFromText;
    }

    public void setLoFromText(String loFromText) {
        this.loFromText = loFromText;
    }

    public String getLoToFile() {
        return this.loToFile;
    }

    public void setLoToFile(String loToFile) {
        this.loToFile = loToFile;
    }
}
