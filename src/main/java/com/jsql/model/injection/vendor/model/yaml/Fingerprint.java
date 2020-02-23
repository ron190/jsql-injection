
package com.jsql.model.injection.vendor.model.yaml;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Fingerprint implements Serializable {

    private List<String> errorMessage = new ArrayList<>();

    public List<String> getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(List<String> errorMessage) {
        this.errorMessage = errorMessage;
    }

}
