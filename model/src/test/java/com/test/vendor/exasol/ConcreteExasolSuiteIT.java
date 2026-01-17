package com.test.vendor.exasol;

import com.test.AbstractTestSuite;
import org.hibernate.cfg.JdbcSettings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import spring.SpringApp;

public abstract class ConcreteExasolSuiteIT extends AbstractTestSuite {

    public ConcreteExasolSuiteIT() {
        var property = SpringApp.get("exasol");
        this.jdbcURL = property.getProperty(JdbcSettings.JAKARTA_JDBC_URL);
        this.jdbcUser = property.getProperty(JdbcSettings.JAKARTA_JDBC_USER);
        this.jdbcPass = property.getProperty(JdbcSettings.JAKARTA_JDBC_PASSWORD);

        this.databaseToInject = "SYS";
        this.tableToInject = "EXA_CLUSTERS";
        this.columnToInject = "CLUSTER_NAME";
        
        this.queryAssertDatabases = "select distinct column_schema from exa_sys_columns";
        this.queryAssertTables = String.format("""
            select column_table
            from exa_sys_columns
            where column_schema = '%s'
        """, this.databaseToInject);
        this.queryAssertColumns = String.format("""
            select column_name
            from exa_sys_columns
            where column_schema = '%s'
            and column_table = '%s'
        """, this.databaseToInject, this.tableToInject);
        this.queryAssertValues = String.format("select %s from %s", this.columnToInject, this.tableToInject);
    }

    @AfterEach
    public void checkVendor() {
        Assertions.assertEquals(
            this.injectionModel.getMediatorVendor().getExasol(),
            this.injectionModel.getMediatorVendor().getVendor()
        );
    }
}
