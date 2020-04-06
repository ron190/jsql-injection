
package com.jsql.model.injection.vendor.model.yaml;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("serial")
public class Normal implements Serializable {

    private String indices = StringUtils.EMPTY;
    private String capacity = StringUtils.EMPTY;
    private String orderBy = StringUtils.EMPTY;

    public String getIndices() {
        return this.indices;
    }

    public void setIndices(String indices) {
        this.indices = indices;
    }

    public String getCapacity() {
        return this.capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getOrderBy() {
        return this.orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }
}
