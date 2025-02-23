package com.jsql.model.accessible.vendor.postgres;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class TempTable implements Serializable {

    private String drop = StringUtils.EMPTY;
    private String add = StringUtils.EMPTY;
    private String fill = StringUtils.EMPTY;

    public String getDrop() {
        return this.drop;
    }

    public void setDrop(String drop) {
        this.drop = drop;
    }

    public String getAdd() {
        return this.add;
    }

    public void setAdd(String add) {
        this.add = add;
    }

    public String getFill() {
        return this.fill;
    }

    public void setFill(String fill) {
        this.fill = fill;
    }
}