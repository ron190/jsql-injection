
package com.jsql.model.injection.vendor.model.yaml;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class File implements Serializable {

    private String privilege = StringUtils.EMPTY;
    private String read = StringUtils.EMPTY;
    private Write write = new Write();

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

    public Write getWrite() {
        return this.write;
    }

    public void setWrite(Write write) {
        this.write = write;
    }
}
