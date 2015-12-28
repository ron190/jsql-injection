package com.jsql.model.vendor;

public class MariaDBStrategy extends MySQLStrategy {
    @Override
    public String getDbLabel() {
        return "MariaDB";
    }
}