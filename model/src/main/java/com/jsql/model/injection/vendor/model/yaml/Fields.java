
package com.jsql.model.injection.vendor.model.yaml;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class Fields implements Serializable {

    private String field = StringUtils.EMPTY;
    private String concat = StringUtils.EMPTY;

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
