
package com.jsql.model.injection.vendor.model.yaml;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("serial")
public class Test implements Serializable {

    private List<String> falses =
        Arrays
        .asList(
            "true = false",
            "true %21= true",
            "false %21= false",
            "1 = 2",
            "1 %21= 1",
            "2 %21= 2"
        );
            
    private List<String> trues =
        Arrays
        .asList(
            "true = true",
            "false = false",
            "true %21= false",
            "1 = 1",
            "2 = 2",
            "1 %21= 2"
        );

    private String initialization = "0%2b1 = 1";
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
            this.trues
            .stream()
            .collect(
                Collectors.joining(System.getProperty("line.separator"))
            );
    }
    
    public void setTruthy(String truthy) {
        this.trues = Arrays.asList(truthy.split("[\r\n]+"));
    }
    
    public String getFalsyAsString() {
        return
            this.falses
            .stream()
            .collect(
                Collectors.joining(System.getProperty("line.separator"))
            );
    }
    
    public void setFalsy(String falsy) {
        this.falses = Arrays.asList(falsy.split("[\r\n]+"));
    }
}
