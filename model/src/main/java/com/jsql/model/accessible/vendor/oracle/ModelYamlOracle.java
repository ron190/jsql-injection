
package com.jsql.model.accessible.vendor.oracle;

import java.io.Serializable;

public class ModelYamlOracle implements Serializable {

    private Rce rce = new Rce();

    public Rce getRce() {
        return this.rce;
    }

    public void setRce(Rce rce) {
        this.rce = rce;
    }
}