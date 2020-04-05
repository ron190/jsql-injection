
package com.jsql.model.injection.vendor.model.yaml;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Create implements Serializable {

    private String content = "";
    private String query = "";

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getQuery() {
        return this.query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
