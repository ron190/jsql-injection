package com.test.vendor.vertica;

import com.test.AbstractTestSuite;

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
        this.jdbcQueryForColumnNames = "select column_name from v_catalog.jdbc_columns where jdbc_columns.schema_name = '"+ this.jsqlDatabaseName +"' and jdbc_columns.table_name = '"+ this.jsqlTableName +"'";
        this.jdbcQueryForValues = "select "+ this.jsqlColumnName +" from "+ this.jsqlTableName;
    }
}
