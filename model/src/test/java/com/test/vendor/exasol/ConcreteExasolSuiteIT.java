package com.test.vendor.exasol;

import com.test.AbstractTestSuite;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;

public abstract class ConcreteExasolSuiteIT extends AbstractTestSuite {

    public ConcreteExasolSuiteIT() {
        this.jdbcURL = "jdbc:exa:jsql-exasol:8563/nocertcheck";
        this.jdbcUser = "sys";
        this.jdbcPass = "exasol";

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
