package com.test.vendor.mimer;

import com.test.AbstractTestSuite;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;

public abstract class ConcreteMimerSuiteIT extends AbstractTestSuite {

    // mimer detects heavy load and will throw SQLException: Operation not allowed. Configured number of users exceeded.
    public ConcreteMimerSuiteIT() {
        this.jdbcURL = "jdbc:mimer://jsql-mimer:1360/mimerdb";
        this.jdbcUser = "SYSADM";
        this.jdbcPass = "SYSADM";

        this.databaseToInject = "SYSTEM";
        this.tableToInject = "ONEROW";
        this.columnToInject = "M";
        
        this.queryAssertDatabases = """
            select distinct table_schema
            from information_schema.tables
        """;
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
        this.queryAssertValues = String.format("select %s from %s.%s", this.columnToInject, this.databaseToInject, this.tableToInject);
    }

    @AfterEach
    public void checkVendor() {
        Assertions.assertEquals(
            this.injectionModel.getMediatorVendor().getMimer(),
            this.injectionModel.getMediatorVendor().getVendor()
        );
    }
}