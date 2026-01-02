package com.test.vendor.vertica;

import com.test.AbstractTestSuite;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;

public abstract class ConcreteVerticaSuiteIT extends AbstractTestSuite {

    public ConcreteVerticaSuiteIT() {

        this.jdbcURL = "jdbc:vertica://jsql-vertica:5433/";
        this.jdbcUser = "dbadmin";
        this.jdbcPass = "password";

        this.jsqlDatabaseName = "v_catalog";
        this.jsqlTableName = "dual";
        this.jsqlColumnName = "dummy";
        
        this.jdbcColumnForDatabaseName = "schema_name";
        this.jdbcColumnForTableName = "table_name";  // prevent tabulation
        this.jdbcColumnForColumnName = "column_name";  // prevent tabulation
        
        this.jdbcQueryForDatabaseNames = "select schema_name from v_catalog.all_tables";
        this.jdbcQueryForTableNames = "select table_name from v_catalog.all_tables where schema_name = '"+ this.jsqlDatabaseName +"'";
        this.jdbcQueryForColumnNames = "select column_name from v_catalog.odbc_columns where odbc_columns.schema_name = '"+ this.jsqlDatabaseName +"' and odbc_columns.table_name = '"+ this.jsqlTableName +"'";
        this.jdbcQueryForValues = "select "+ this.jsqlColumnName +" from "+ this.jsqlTableName;
    }

    @AfterEach
    public void checkVendor() {
        Assertions.assertEquals(
            this.injectionModel.getMediatorVendor().getVertica(),
            this.injectionModel.getMediatorVendor().getVendor()
        );
    }
}
