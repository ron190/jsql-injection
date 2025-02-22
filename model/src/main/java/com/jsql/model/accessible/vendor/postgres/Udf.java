package com.jsql.model.accessible.vendor.postgres;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class Udf implements Serializable {

    private Wal wal = new Wal();
    private Extension extension = new Extension();
    private String plpython = StringUtils.EMPTY;
    private String plperlu = StringUtils.EMPTY;
    private String plsh = StringUtils.EMPTY;
    private String runFunc = StringUtils.EMPTY;
    private String dropFunc = StringUtils.EMPTY;
    private Sql sql = new Sql();

    public Extension getExtension() {
        return this.extension;
    }

    public void setExtension(Extension extension) {
        this.extension = extension;
    }

    public String getPlpython() {
        return this.plpython;
    }

    public void setPlpython(String plpython) {
        this.plpython = plpython;
    }

    public String getPlperlu() {
        return this.plperlu;
    }

    public void setPlperlu(String plperlu) {
        this.plperlu = plperlu;
    }

    public String getPlsh() {
        return this.plsh;
    }

    public void setPlsh(String plsh) {
        this.plsh = plsh;
    }

    public String getRunFunc() {
        return this.runFunc;
    }

    public void setRunFunc(String runFunc) {
        this.runFunc = runFunc;
    }

    public Sql getSql() {
        return this.sql;
    }

    public void setSql(Sql sql) {
        this.sql = sql;
    }

    public String getDropFunc() {
        return this.dropFunc;
    }

    public void setDropFunc(String dropFunc) {
        this.dropFunc = dropFunc;
    }

    public Wal getWal() {
        return this.wal;
    }

    public void setWal(Wal wal) {
        this.wal = wal;
    }
}