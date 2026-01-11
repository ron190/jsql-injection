package com.test.vendor.exasol;

import com.test.AbstractTestSuite;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;

public abstract class ConcreteExasolSuiteIT extends AbstractTestSuite {

    public ConcreteExasolSuiteIT() {

        this.jdbcURL = "jdbc:exa:jsql-exasol:8563/nocertcheck";
        this.jdbcUser = "sys";
        this.jdbcPass = "exasol";

        this.jsqlDatabaseName = "SYS";
        this.jsqlTableName = "EXA_CLUSTERS";
        this.jsqlColumnName = "CLUSTER_NAME";
        
        this.jdbcColumnForDatabaseName = "column_schema";
        this.jdbcColumnForTableName = "column_table";
        this.jdbcColumnForColumnName = "column_name";
        
        this.jdbcQueryForDatabaseNames = "select distinct column_schema from exa_sys_columns";
        this.jdbcQueryForTableNames = "select column_table from exa_sys_columns where column_schema = '"+ this.jsqlDatabaseName +"'";
        this.jdbcQueryForColumnNames = "select column_name from exa_sys_columns where column_schema = '"+ this.jsqlDatabaseName +"' and column_table = '"+ this.jsqlTableName +"'";
        this.jdbcQueryForValues = "select "+ this.jsqlColumnName +" from "+ this.jsqlTableName;
    }

    @AfterEach
    public void checkVendor() {
        Assertions.assertEquals(
            this.injectionModel.getMediatorVendor().getExasol(),
            this.injectionModel.getMediatorVendor().getVendor()
        );
    }
}
