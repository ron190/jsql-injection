package com.jsql.model.accessible.engine.sqlserver;

import org.apache.commons.lang3.StringUtils;

public class File {

    private String read = StringUtils.EMPTY;

    public String getRead() {
        return this.read;
    }

    public void setRead(String read) {
        this.read = read;
    }
}
