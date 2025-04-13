
package com.jsql.model.injection.vendor.model.yaml;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Test implements Serializable {

    private List<String> falsyBinary = new ArrayList<>();  // vendor specific
    private List<String> falsy = Arrays.asList(
        "'a' = 'b'",
        "'a' %21= 'a'",
        "'b' %21= 'b'",
        "1 = 2",
        "1 %21= 1",
        "2 %21= 2"
    );
            
    private List<String> truthyBinary = new ArrayList<>();  // vendor specific
    private List<String> truthy = Arrays.asList(
        "'a' = 'a'",
        "'b' = 'b'",
        "'a' %21= 'b'",
        "1 = 1",
        "2 = 2",
        "1 %21= 2"
    );

    private String init = "0%2b1 = 1";
    private String bit = StringUtils.EMPTY;
    private String binary = StringUtils.EMPTY;
    private String length = StringUtils.EMPTY;

    public List<String> getTruthyBinary() {
        return this.truthyBinary;
    }

    public void setTruthyBinary(List<String> truthyBinary) {
        this.truthyBinary = truthyBinary;
    }

    public List<String> getFalsyBinary() {
        return this.falsyBinary;
    }

    public void setFalsyBinary(List<String> falsyBinary) {
        this.falsyBinary = falsyBinary;
    }

    public List<String> getTruthy() {
        return this.truthy;
    }

    public void setTruthy(List<String> truthy) {
        this.truthy = truthy;
    }

    public List<String> getFalsy() {
        return this.falsy;
    }

    public void setFalsy(List<String> falsy) {
        this.falsy = falsy;
    }

    public String getInit() {
        return this.init;
    }

    public void setInit(String init) {
        this.init = init;
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
        return this.truthy.stream().collect(Collectors.joining(System.lineSeparator()));
    }
    
    public void setTruthy(String truthy) {
        this.truthy = Arrays.asList(truthy.split("[\r\n]+"));
    }
    
    public String getFalsyAsString() {
        return this.falsy.stream().collect(Collectors.joining(System.lineSeparator()));
    }
    
    public void setFalsy(String falsy) {
        this.falsy = Arrays.asList(falsy.split("[\r\n]+"));
    }

    public String getBinary() {
        return this.binary;
    }

    public void setBinary(String binary) {
        this.binary = binary;
    }
}
