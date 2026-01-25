package com.jsql.model.accessible.engine.postgres;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class Program implements Serializable {

    private String run = StringUtils.EMPTY;
    private String getResult = StringUtils.EMPTY;

    public String getRun() {
        return this.run;
    }

    public void setRun(String run) {
        this.run = run;
    }

    public String getGetResult() {
        return this.getResult;
    }

    public void setGetResult(String getResult) {
        this.getResult = getResult;
    }
}
