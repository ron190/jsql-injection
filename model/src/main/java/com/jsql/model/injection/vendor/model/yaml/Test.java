
package com.jsql.model.injection.vendor.model.yaml;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Test implements Serializable {

    private static final String REGEX_ARRAY = "[\r\n]+";

    private List<String> falsyBin = new ArrayList<>();  // vendor specific
    private List<String> falsyBit = Arrays.asList(
        "'a' = 'b'",
        "'a' %21= 'a'",
        "'b' %21= 'b'",
        "1 = 2",
        "1 %21= 1",
        "2 %21= 2"
    );
            
    private List<String> truthyBin = new ArrayList<>();  // vendor specific
    private List<String> truthyBit = Arrays.asList(
        "'a' = 'a'",
        "'b' = 'b'",
        "'a' %21= 'b'",
        "1 = 1",
        "2 = 2",
        "1 %21= 2"
    );

    private String init = "0%2b1 = 1";
    private String bit = StringUtils.EMPTY;
    private String bin = StringUtils.EMPTY;
    private String length = StringUtils.EMPTY;

    public List<String> getTruthyBin() {
        return this.truthyBin;
    }

    public void setTruthyBin(List<String> truthyBin) {
        this.truthyBin = truthyBin;
    }

    public List<String> getFalsyBin() {
        return this.falsyBin;
    }

    public void setFalsyBin(List<String> falsyBin) {
        this.falsyBin = falsyBin;
    }

    public List<String> getTruthyBit() {
        return this.truthyBit;
    }

    public void setTruthyBit(List<String> truthyBit) {
        this.truthyBit = truthyBit;
    }

    public List<String> getFalsyBit() {
        return this.falsyBit;
    }

    public void setFalsyBit(List<String> falsyBit) {
        this.falsyBit = falsyBit;
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

    public String getTruthyBitAsString() {
        return this.truthyBit.stream().collect(Collectors.joining(System.lineSeparator()));
    }
    
    public void setTruthyBit(String truthyBit) {
        this.truthyBit = Arrays.asList(truthyBit.split(Test.REGEX_ARRAY));
    }
    
    public String getFalsyBitAsString() {
        return this.falsyBit.stream().collect(Collectors.joining(System.lineSeparator()));
    }
    
    public void setFalsyBit(String falsyBit) {
        this.falsyBit = Arrays.asList(falsyBit.split(Test.REGEX_ARRAY));
    }

    public String getTruthyBinAsString() {
        return this.truthyBin.stream().collect(Collectors.joining(System.lineSeparator()));
    }

    public void setTruthyBin(String truthyBin) {
        this.truthyBin = Arrays.asList(truthyBin.split(Test.REGEX_ARRAY));
    }

    public String getFalsyBinAsString() {
        return this.falsyBin.stream().collect(Collectors.joining(System.lineSeparator()));
    }

    public void setFalsyBin(String falsyBin) {
        this.falsyBin = Arrays.asList(falsyBin.split(Test.REGEX_ARRAY));
    }

    public String getBin() {
        return this.bin;
    }

    public void setBin(String bin) {
        this.bin = bin;
    }
}
