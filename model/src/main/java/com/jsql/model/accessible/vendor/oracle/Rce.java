
package com.jsql.model.accessible.vendor.oracle;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class Rce implements Serializable {

    private String dropSource = StringUtils.EMPTY;
    private String dropFunc = StringUtils.EMPTY;
    private String addSource = StringUtils.EMPTY;
    private String addFunc = StringUtils.EMPTY;
    private String grant = StringUtils.EMPTY;
    private String confirm = StringUtils.EMPTY;
    private String runCmd = StringUtils.EMPTY;

    public String getDropSource() {
        return this.dropSource;
    }

    public void setDropSource(String dropSource) {
        this.dropSource = dropSource;
    }

    public String getDropFunc() {
        return this.dropFunc;
    }

    public void setDropFunc(String dropFunc) {
        this.dropFunc = dropFunc;
    }

    public String getAddSource() {
        return this.addSource;
    }

    public void setAddSource(String addSource) {
        this.addSource = addSource;
    }

    public String getAddFunc() {
        return this.addFunc;
    }

    public void setAddFunc(String addFunc) {
        this.addFunc = addFunc;
    }

    public String getGrant() {
        return this.grant;
    }

    public void setGrant(String grant) {
        this.grant = grant;
    }

    public String getConfirm() {
        return this.confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }

    public String getRunCmd() {
        return this.runCmd;
    }

    public void setRunCmd(String runCmd) {
        this.runCmd = runCmd;
    }
}