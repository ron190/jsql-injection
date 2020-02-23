
package com.jsql.model.injection.vendor.model.yaml;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Fields implements Serializable {

    private String field = "";
    private String concat = "";

    public String getField() {
        return this.field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getConcat() {
        return this.concat;
    }

    public void setConcat(String concat) {
        this.concat = concat;
    }

}
