package com.jsql.model.accessible.vendor.mysql;

import org.apache.commons.lang3.StringUtils;

public class AddFunction {

    private String drop = StringUtils.EMPTY;
    private String create = StringUtils.EMPTY;
    private String confirm = StringUtils.EMPTY;

    public String getDrop() {
        return this.drop;
    }

    public void setDrop(String drop) {
        this.drop = drop;
    }

    public String getCreate() {
        return this.create;
    }

    public void setCreate(String create) {
        this.create = create;
    }

    public String getConfirm() {
        return this.confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }
}