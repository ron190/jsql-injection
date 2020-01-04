
package com.jsql.model.injection.vendor.model.yaml;

import java.util.ArrayList;
import java.util.List;

public class Fingerprint {

    private List<String> errorMessage = new ArrayList<>();

    public List<String> getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(List<String> errorMessage) {
        this.errorMessage = errorMessage;
    }

}
