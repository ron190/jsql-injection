
package com.jsql.model.injection.vendor.model.yaml;

public class Boolean {

    private Test test = new Test();
    private String blind = "";
    private String time = "";
    private String modeAnd = "";
    private String modeOr = "";

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
