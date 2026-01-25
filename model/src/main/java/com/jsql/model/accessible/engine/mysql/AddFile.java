package com.jsql.model.accessible.engine.mysql;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class AddFile implements Serializable {

    private String queryBody = StringUtils.EMPTY;
    private String netshare = StringUtils.EMPTY;
    private TempTable tempTable = new TempTable();

    public String getQueryBody() {
        return this.queryBody;
    }

    public void setQueryBody(String queryBody) {
        this.queryBody = queryBody;
    }

    public String getNetshare() {
        return this.netshare;
    }

    public void setNetshare(String netshare) {
        this.netshare = netshare;
    }

    public TempTable getTempTable() {
        return this.tempTable;
    }

    public void setTempTable(TempTable tempTable) {
        this.tempTable = tempTable;
    }
}