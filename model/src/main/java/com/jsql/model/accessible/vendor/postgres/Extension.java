package com.jsql.model.accessible.vendor.postgres;

import org.apache.commons.lang3.StringUtils;

public class Extension {

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