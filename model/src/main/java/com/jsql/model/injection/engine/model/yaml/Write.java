
package com.jsql.model.injection.engine.model.yaml;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class Write implements Serializable {

    private String body = StringUtils.EMPTY;
    private String path = StringUtils.EMPTY;

    public String getBody() {
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
