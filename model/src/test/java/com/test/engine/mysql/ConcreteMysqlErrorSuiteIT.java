package com.test.engine.mysql;

import org.hibernate.cfg.JdbcSettings;
import spring.SpringApp;

public abstract class ConcreteMysqlErrorSuiteIT extends ConcreteMysqlSuiteIT {

    public ConcreteMysqlErrorSuiteIT() {
        var property = SpringApp.get("mysql-error");
        this.jdbcURL = property.getProperty(JdbcSettings.JAKARTA_JDBC_URL);
    }
}
