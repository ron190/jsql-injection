package com.test.vendor.virtuoso;

import com.test.AbstractTestSuite;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;

public abstract class ConcreteVirtuosoSuiteIT extends AbstractTestSuite {

    public ConcreteVirtuosoSuiteIT() {
        this.jdbcURL = "jdbc:virtuoso://jsql-virtuoso:1111";
        this.jdbcUser = "dba";
        this.jdbcPass = "dba";

        this.databaseToInject = "DBA";
        this.tableToInject = "LDLOCK";
        this.columnToInject = "id";
        
        this.queryAssertDatabases = "select table_schema from information_schema.tables";
        this.queryAssertTables = String.format("""
            select table_name
            from information_schema.tables
            where table_schema = '%s'
        """, this.databaseToInject);
        this.queryAssertColumns = String.format("""
            select column_name
            from information_schema.columns
            where table_schema = '%s'
            and table_name = '%s'
        """, this.databaseToInject, this.tableToInject);
        this.queryAssertValues = String.format("select %s from %s", this.columnToInject, this.tableToInject);
    }

    @AfterEach
    public void checkVendor() {
        Assertions.assertEquals(
            this.injectionModel.getMediatorVendor().getVirtuoso(),
            this.injectionModel.getMediatorVendor().getVendor()
        );
    }
}
