package com.test.vendor.vertica;

import com.test.AbstractTestSuite;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;

public abstract class ConcreteVerticaSuiteIT extends AbstractTestSuite {

    public ConcreteVerticaSuiteIT() {
        this.jdbcURL = "jdbc:vertica://jsql-vertica:5433/";
        this.jdbcUser = "dbadmin";
        this.jdbcPass = "password";

        this.databaseToInject = "v_catalog";
        this.tableToInject = "dual";
        this.columnToInject = "dummy";
        
        this.queryAssertDatabases = "select schema_name from v_catalog.all_tables";
        this.queryAssertTables = String.format("""
            select table_name
            from v_catalog.all_tables
            where schema_name = '%s'
        """, this.databaseToInject);
        this.queryAssertColumns = String.format("""
            select column_name
            from v_catalog.odbc_columns
            where odbc_columns.schema_name = '%s'
            and odbc_columns.table_name = '%s'
        """, this.databaseToInject, this.tableToInject);
        this.queryAssertValues = String.format("select %s from %s", this.columnToInject, this.tableToInject);
    }

    @AfterEach
    public void checkVendor() {
        Assertions.assertEquals(
            this.injectionModel.getMediatorVendor().getVertica(),
            this.injectionModel.getMediatorVendor().getVendor()
        );
    }
}
