package com.jsql.model.accessible.vendor.postgres;

import org.apache.commons.lang3.StringUtils;

public class Shell {

    private String confirmWeb = StringUtils.EMPTY;
    private String confirmSql = StringUtils.EMPTY;

    public String getConfirmWeb() {
        return this.confirmWeb;
    }

    public void setConfirmWeb(String confirmWeb) {
        this.confirmWeb = confirmWeb;
    }

    public String getConfirmSql() {
        return this.confirmSql;
    }

    public void setConfirmSql(String confirmSql) {
        this.confirmSql = confirmSql;
    }
}