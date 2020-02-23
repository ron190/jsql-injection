
package com.jsql.model.injection.vendor.model.yaml;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Normal implements Serializable {

    private String indices = "";
    private String capacity = "";
    private String orderBy = "";

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
