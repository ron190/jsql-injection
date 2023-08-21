
package com.jsql.model.injection.vendor.model.yaml;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class Boolean implements Serializable {

    private Test test = new Test();
    private String blind = StringUtils.EMPTY;
    private String time = StringUtils.EMPTY;
    private String multibit = StringUtils.EMPTY;
    private String modeAnd = "and";
    private String modeOr = "or";
    private String modeStacked = ";";

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

    public String getModeStacked() {
        return this.modeStacked;
    }

    public void setModeStacked(String modeStacked) {
        this.modeStacked = modeStacked;
    }

    public String getMultibit() {
        return multibit;
    }

    public void setMultibit(String multibit) {
        this.multibit = multibit;
    }
}
