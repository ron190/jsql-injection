
package com.jsql.model.injection.vendor.model.yaml;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Error implements Serializable {

    private List<Method> method = new ArrayList<>();

    public List<Method> getMethod() {
        return this.method;
    }

    public void setMethod(List<Method> method) {
        this.method = method;
    }
}
