package com.test.vendor.oracle;

import com.test.AbstractTestSuite;
import org.hibernate.cfg.JdbcSettings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import spring.SpringApp;

public abstract class ConcreteOracleSuiteIT extends AbstractTestSuite {

    public ConcreteOracleSuiteIT() {
        this.config();
    }
    
    public void config() {
        this.jdbcURL = SpringApp.propsOracle.getProperty(JdbcSettings.JAKARTA_JDBC_URL);
        this.jdbcUser = SpringApp.propsOracle.getProperty(JdbcSettings.JAKARTA_JDBC_USER);
        this.jdbcPass = SpringApp.propsOracle.getProperty(JdbcSettings.JAKARTA_JDBC_PASSWORD);

        this.databaseToInject = "XDB";
        this.tableToInject = "APP_USERS_AND_ROLES";
        this.columnToInject = "NAME";
        
        this.queryAssertDatabases = "SELECT distinct owner FROM all_tables";
        this.queryAssertTables = "SELECT distinct table_name FROM all_tables where owner='XDB'";
        this.queryAssertColumns = """
            SELECT distinct column_name
            FROM all_tab_columns
            where owner='XDB' and table_name='APP_USERS_AND_ROLES'
        """;
        this.queryAssertValues = "SELECT distinct NAME FROM XDB.APP_USERS_AND_ROLES";
    }

    @AfterEach
    public void checkVendor() {
        Assertions.assertEquals(
            this.injectionModel.getMediatorVendor().getOracle(),
            this.injectionModel.getMediatorVendor().getVendor()
        );
    }
}
