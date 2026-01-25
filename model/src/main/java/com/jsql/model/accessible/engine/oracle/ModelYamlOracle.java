
package com.jsql.model.accessible.engine.oracle;

import java.io.Serializable;

public class ModelYamlOracle implements Serializable {

    private Udf udf = new Udf();

    public Udf getUdf() {
        return this.udf;
    }

    public void setUdf(Udf udf) {
        this.udf = udf;
    }
}