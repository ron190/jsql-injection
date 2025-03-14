package com.jsql.model.accessible.vendor.postgres;

import java.io.Serializable;

public class Write implements Serializable {

    private LargeObject largeObject = new LargeObject();
    private TempTable tempTable = new TempTable();

    public LargeObject getLargeObject() {
        return this.largeObject;
    }

    public void setLargeObject(LargeObject largeObject) {
        this.largeObject = largeObject;
    }

    public TempTable getTempTable() {
        return this.tempTable;
    }

    public void setTempTable(TempTable tempTable) {
        this.tempTable = tempTable;
    }
}