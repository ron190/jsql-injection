
package com.jsql.model.injection.vendor.model.yaml;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Test implements Serializable {

    private List<String> falses = new ArrayList<>();
    private List<String> trues = new ArrayList<>();
    private String initialization = "";
    private String bit = "";
    private String length = "";

    public List<String> getTrues() {
        return this.trues;
    }

    public void setTrues(List<String> trues) {
        this.trues = trues;
    }

    public List<String> getFalses() {
        return this.falses;
    }

    public void setFalses(List<String> falses) {
        this.falses = falses;
    }

    public String getInitialization() {
        return this.initialization;
    }

    public void setInitialization(String initialization) {
        this.initialization = initialization;
    }

    public String getBit() {
        return this.bit;
    }

    public void setBit(String bit) {
        this.bit = bit;
    }

    public String getLength() {
        return this.length;
    }

    public void setLength(String length) {
        this.length = length;
    }

}
