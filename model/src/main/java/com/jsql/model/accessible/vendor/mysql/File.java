package com.jsql.model.accessible.vendor.mysql;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class File implements Serializable {

    private String privilege = StringUtils.EMPTY;
    private String read = StringUtils.EMPTY;

    public String getPrivilege() {
        return this.privilege;
    }

    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }

    public String getRead() {
        return this.read;
    }

    public void setRead(String read) {
        this.read = read;
    }
}