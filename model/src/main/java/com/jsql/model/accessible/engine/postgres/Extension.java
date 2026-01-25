package com.jsql.model.accessible.engine.postgres;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class Extension implements Serializable {

    private String create = StringUtils.EMPTY;
    private String languages = StringUtils.EMPTY;

    public String getCreate() {
        return this.create;
    }

    public void setCreate(String create) {
        this.create = create;
    }

    public String getLanguages() {
        return this.languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }
}