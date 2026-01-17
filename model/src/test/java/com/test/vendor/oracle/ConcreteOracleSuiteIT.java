package com.test.vendor.oracle;

import com.test.AbstractTestSuite;
import org.hibernate.cfg.JdbcSettings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import spring.SpringApp;

public abstract class ConcreteOracleSuiteIT extends AbstractTestSuite {

    public ConcreteOracleSuiteIT() {
        var property = SpringApp.get("oracle");
        this.jdbcURL = property.getProperty(JdbcSettings.JAKARTA_JDBC_URL);
        this.jdbcUser = property.getProperty(JdbcSettings.JAKARTA_JDBC_USER);
        this.jdbcPass = property.getProperty(JdbcSettings.JAKARTA_JDBC_PASSWORD);

        this.databaseToInject = "XDB";
        this.tableToInject = "APP_USERS_AND_ROLES";
        this.columnToInject = "NAME";
        
        this.queryAssertDatabases = "SELECT distinct owner FROM all_tables";
        this.queryAssertTables = String.format("SELECT distinct table_name FROM all_tables where owner='%s'", this.databaseToInject);
        this.queryAssertColumns = String.format("""
            SELECT distinct column_name
            FROM all_tab_columns
            where owner='%s' and table_name='%s'
        """, this.databaseToInject, this.tableToInject);
        this.queryAssertValues = String.format("select distinct %s from %s.%s", this.columnToInject, this.databaseToInject, this.tableToInject);
    }

    @AfterEach
    public void checkVendor() {
        Assertions.assertEquals(
            this.injectionModel.getMediatorVendor().getOracle(),
            this.injectionModel.getMediatorVendor().getVendor()
        );
    }
}
