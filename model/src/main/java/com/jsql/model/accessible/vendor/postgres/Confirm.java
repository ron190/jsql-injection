package com.jsql.model.accessible.vendor.postgres;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class Confirm implements Serializable {

    private String addFunc = StringUtils.EMPTY;
    private String funcExists = StringUtils.EMPTY;

    public String getAddFunc() {
        return this.addFunc;
    }

    public void setAddFunc(String addFunc) {
        this.addFunc = addFunc;
    }

    public String getFuncExists() {
        return this.funcExists;
    }

    public void setFuncExists(String funcExists) {
        this.funcExists = funcExists;
    }
}