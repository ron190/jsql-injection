
package com.jsql.model.injection.vendor.model.yaml;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("serial")
public class Boolean implements Serializable {

    private Test test = new Test();
    private String blind = StringUtils.EMPTY;
    private String time = StringUtils.EMPTY;
    private String modeAnd = "and";
    private String modeOr = "or";

    public Test getTest() {
        return this.test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public String getBlind() {
        return this.blind;
    }

    public void setBlind(String blind) {
        this.blind = blind;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getModeAnd() {
        return this.modeAnd;
    }

    public void setModeAnd(String modeAnd) {
        this.modeAnd = modeAnd;
    }

    public String getModeOr() {
        return this.modeOr;
    }

    public void setModeOr(String modeOr) {
        this.modeOr = modeOr;
    }
}
