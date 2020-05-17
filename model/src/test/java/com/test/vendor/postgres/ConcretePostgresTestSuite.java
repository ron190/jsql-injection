package com.test.vendor.postgres;

import com.test.AbstractTestSuite;

public abstract class ConcretePostgresTestSuite extends AbstractTestSuite {

    public ConcretePostgresTestSuite() {

        this.jdbcURL = "jdbc:postgresql://jsql-postgres:5432/";
        this.jdbcUser = "postgres";
        this.jdbcPass = "my-secret-pw";
        this.jsqlDatabaseName = "public";
        this.jsqlTableName = "student";
        this.jsqlColumnName = "Student_Id";
        
        this.jdbcColumnForDatabaseName = "table_schema";
        this.jdbcColumnForTableName = "table_name";
        this.jdbcColumnForColumnName = "column_name";
        
        this.jdbcQueryForDatabaseNames = "SELECT table_schema FROM information_schema.tables";
        this.jdbcQueryForTableNames = "select TABLE_NAME from INFORMATION_SCHEMA.tables where TABLE_SCHEMA='"+ this.jsqlDatabaseName +"'";
        this.jdbcQueryForColumnNames = "select COLUMN_NAME from information_schema.columns where TABLE_SCHEMA='"+ this.jsqlDatabaseName +"' and TABLE_NAME='"+ this.jsqlTableName +"'";
        this.jdbcQueryForValues = "select "+ this.jsqlColumnName +" from "+ this.jsqlDatabaseName +"."+ this.jsqlTableName;
    }
}
