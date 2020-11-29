
package com.jsql.model.injection.vendor.model.yaml;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("serial")
public class Test implements Serializable {

    private List<String> falses = new ArrayList<>();
    private List<String> trues = new ArrayList<>();
    private String initialization = StringUtils.EMPTY;
    private String bit = StringUtils.EMPTY;
    private String length = StringUtils.EMPTY;

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

    public String getTruthyAsString() {
        return 
            trues
            .stream()
            .collect(
                Collectors.joining(System.getProperty("line.separator"))
            );
    }
    
    public void setTruthy(String truthy) {
        trues = Arrays.asList(truthy.split("[\r\n]+"));
    }
    
    public String getFalsyAsString() {
        return 
            falses
            .stream()
            .collect(
                Collectors.joining(System.getProperty("line.separator"))
            );
    }
    
    public void setFalsy(String falsy) {
        falses = Arrays.asList(falsy.split("[\r\n]+"));
    }
}
