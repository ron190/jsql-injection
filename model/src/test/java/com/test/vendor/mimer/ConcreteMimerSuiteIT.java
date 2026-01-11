package com.test.vendor.mimer;

import com.test.AbstractTestSuite;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;

public abstract class ConcreteMimerSuiteIT extends AbstractTestSuite {

    public ConcreteMimerSuiteIT() {
        this.jdbcURL = "jdbc:mimer://jsql-mimer:1360/mimerdb";
        this.jdbcUser = "SYSADM";
        this.jdbcPass = "SYSADM";

        this.databaseToInject = "MIMER";
        this.tableToInject = "ODBC_TABLE_TYPES";
        this.columnToInject = "TABLE_TYPE";
        
        this.queryAssertDatabases = """
            select distinct schema_name 
            from information_schema.tables 
            right join information_schema.schemata on schema_name = table_schema
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