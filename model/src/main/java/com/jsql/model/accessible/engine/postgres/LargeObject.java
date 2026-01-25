package com.jsql.model.accessible.engine.postgres;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class LargeObject implements Serializable {

    private String fromPath = StringUtils.EMPTY;
    private String toText = StringUtils.EMPTY;
    private String fromText = StringUtils.EMPTY;
    private String toFile = StringUtils.EMPTY;

    public String getFromPath() {
        return this.fromPath;
    }

    public void setFromPath(String fromPath) {
        this.fromPath = fromPath;
    }

    public String getToText() {
        return this.toText;
    }

    public void setToText(String toText) {
        this.toText = toText;
    }

    public String getFromText() {
        return this.fromText;
    }

    public void setFromText(String fromText) {
        this.fromText = fromText;
    }

    public String getToFile() {
        return this.toFile;
    }

    public void setToFile(String toFile) {
        this.toFile = toFile;
    }
}