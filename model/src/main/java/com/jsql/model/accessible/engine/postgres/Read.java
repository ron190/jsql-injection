package com.jsql.model.accessible.engine.postgres;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class Read implements Serializable {

    private String fromDataFolder = StringUtils.EMPTY;
    private LargeObject largeObject = new LargeObject();
    private String fromTempTable = StringUtils.EMPTY;

    public String getFromDataFolder() {
        return this.fromDataFolder;
    }

    public void setFromDataFolder(String fromDataFolder) {
        this.fromDataFolder = fromDataFolder;
    }

    public LargeObject getLargeObject() {
        return this.largeObject;
    }

    public void setLargeObject(LargeObject largeObject) {
        this.largeObject = largeObject;
    }

    public String getFromTempTable() {
        return this.fromTempTable;
    }

    public void setFromTempTable(String fromTempTable) {
        this.fromTempTable = fromTempTable;
    }
}