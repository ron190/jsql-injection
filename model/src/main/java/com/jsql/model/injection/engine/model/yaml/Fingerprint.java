
package com.jsql.model.injection.engine.model.yaml;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Fingerprint implements Serializable {

    private List<String> errorMessage = new ArrayList<>();
    private String orderByErrorMessage = StringUtils.EMPTY;
    private String engineSpecific = StringUtils.EMPTY;

    public List<String> getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(List<String> errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getOrderByErrorMessage() {
        return this.orderByErrorMessage;
    }

    public void setOrderByErrorMessage(String orderByErrorMessage) {
        this.orderByErrorMessage = orderByErrorMessage;
    }
    
    public String getErrorMessageAsString() {
        return this.errorMessage.stream().collect(Collectors.joining(System.lineSeparator()));
    }
    
    public void setErrorMessageAsString(String errorMessage) {
        this.errorMessage = Arrays.asList(errorMessage.split("[\r\n]+"));
    }

    public String getEngineSpecific() {
        return this.engineSpecific;
    }

    public void setEngineSpecific(String engineSpecific) {
        this.engineSpecific = engineSpecific;
    }
}
