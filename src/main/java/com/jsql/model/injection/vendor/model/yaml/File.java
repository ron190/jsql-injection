
package com.jsql.model.injection.vendor.model.yaml;


public class File {

    private String privilege;
    private String read;
    private Create create;

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
