package com.test.vendor.mysql;

import org.hibernate.cfg.JdbcSettings;
import spring.SpringTargetApplication;

public abstract class ConcreteMySqlErrorSuiteIT extends ConcreteMySqlSuiteIT {

    @Override
    public void config() {
        
        super.config();

        this.jdbcURL = SpringTargetApplication.propsMysqlError.getProperty(JdbcSettings.JAKARTA_JDBC_URL);
    }
}
