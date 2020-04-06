
package com.jsql.model.injection.vendor.model.yaml;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("serial")
public class File implements Serializable {

    private String privilege = StringUtils.EMPTY;
    private String read = StringUtils.EMPTY;
    private Create create = new Create();

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

    public Create getCreate() {
        return this.create;
    }

    public void setCreate(Create create) {
        this.create = create;
    }
}
