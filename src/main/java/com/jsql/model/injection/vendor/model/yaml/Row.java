
package com.jsql.model.injection.vendor.model.yaml;


public class Row {

    private String query;
    private Fields fields;

    public String getQuery() {
        return this.query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Fields getFields() {
        return this.fields;
    }

    public void setFields(Fields fields) {
        this.fields = fields;
    }

}
