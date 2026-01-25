package com.jsql.model.accessible.engine.hsqldb;

import org.apache.commons.lang3.StringUtils;

public class File {

    private String write = StringUtils.EMPTY;
    private Read read = new Read();

    public String getWrite() {
        return this.write;
    }

    public void setWrite(String write) {
        this.write = write;
    }

    public Read getRead() {
        return this.read;
    }

    public void setRead(Read read) {
        this.read = read;
    }
}