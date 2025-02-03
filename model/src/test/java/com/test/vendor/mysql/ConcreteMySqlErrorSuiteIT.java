package com.test.vendor.mysql;

import org.hibernate.cfg.JdbcSettings;
import spring.SpringApp;

public abstract class ConcreteMySqlErrorSuiteIT extends ConcreteMySqlSuiteIT {

    @Override
    public void config() {
        super.config();
        this.jdbcURL = SpringApp.propsMysqlError.getProperty(JdbcSettings.JAKARTA_JDBC_URL);
    }
}
