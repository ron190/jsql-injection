
package com.jsql.model.injection.vendor.model.yaml;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("serial")
public class Fingerprint implements Serializable {

    private List<String> errorMessage = new ArrayList<>();
    private String orderByErrorMessage = StringUtils.EMPTY;

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
        return
            this.errorMessage
            .stream()
            .collect(
                Collectors.joining(System.getProperty("line.separator"))
            );
    }
    
    public void setErrorMessageAsString(String errorMessage) {
        this.errorMessage = Arrays.asList(errorMessage.split("[\r\n]+"));
    }
}
