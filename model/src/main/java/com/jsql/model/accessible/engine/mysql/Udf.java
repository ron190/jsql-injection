package com.jsql.model.accessible.engine.mysql;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class Udf implements Serializable {

    private String pathPlugin = StringUtils.EMPTY;
    private String osMachine = StringUtils.EMPTY;
    private AddFile addFile = new AddFile();
    private AddFunction addFunction = new AddFunction();
    private String runCmd = StringUtils.EMPTY;

    public String getPathPlugin() {
        return this.pathPlugin;
    }

    public void setPathPlugin(String pathPlugin) {
        this.pathPlugin = pathPlugin;
    }

    public String getOsMachine() {
        return this.osMachine;
    }

    public void setOsMachine(String osMachine) {
        this.osMachine = osMachine;
    }

    public AddFile getAddFile() {
        return this.addFile;
    }

    public void setAddFile(AddFile addFile) {
        this.addFile = addFile;
    }

    public AddFunction getAddFunction() {
        return this.addFunction;
    }

    public void setAddFunction(AddFunction addFunction) {
        this.addFunction = addFunction;
    }

    public String getRunCmd() {
        return this.runCmd;
    }

    public void setRunCmd(String runCmd) {
        this.runCmd = runCmd;
    }
}