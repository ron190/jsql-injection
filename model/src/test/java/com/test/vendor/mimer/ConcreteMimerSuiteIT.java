package com.test.vendor.mimer;

import com.test.AbstractTestSuite;

public abstract class ConcreteMimerSuiteIT extends AbstractTestSuite {

    public ConcreteMimerSuiteIT() {

        this.jdbcURL = "jdbc:mimer://jsql-mimer:1360/mimerdb";
        this.jdbcUser = "SYSADM";
        this.jdbcPass = "SYSADM";

        this.jsqlDatabaseName = "MIMER";
        this.jsqlTableName = "JDBC2_UDTS";
        this.jsqlColumnName = "TYPE_NAME";
        
        this.jdbcColumnForDatabaseName = "schema_name";
        this.jdbcColumnForTableName = "table_name";
        this.jdbcColumnForColumnName = "column_name";
        
        this.jdbcQueryForDatabaseNames = "select distinct schema_name from information_schema.tables right join information_schema.schemata on schema_name = table_schema";
        this.jdbcQueryForTableNames = "select table_name from information_schema.tables where table_schema = '"+ this.jsqlDatabaseName +"'";
        this.jdbcQueryForColumnNames = "select column_name from information_schema.columns where table_schema = '"+ this.jsqlDatabaseName +"' and table_name = '"+ this.jsqlTableName +"'";
        this.jdbcQueryForValues = "select "+ this.jsqlColumnName +" from "+ this.jsqlDatabaseName +"."+ this.jsqlTableName;
    }
}