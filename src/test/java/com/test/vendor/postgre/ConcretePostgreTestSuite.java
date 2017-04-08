package com.test.vendor.postgre;

import com.test.AbstractTestSuite;

public class ConcretePostgreTestSuite extends AbstractTestSuite {

    public ConcretePostgreTestSuite () {
        this.jdbcURL = "jdbc:postgresql://"+ AbstractTestSuite.HOSTNAME +":5432/postgres";
        this.jdbcUser = "postgres";
        this.jdbcPass = "pg";
        this.jsqlDatabaseName = "information_schema";
        this.jsqlTableName = "sql_parts";
        this.jsqlColumnName = "feature_id";
        
        this.jdbcColumnForDatabaseName = "table_schema";
        this.jdbcColumnForTableName = "table_name";
        this.jdbcColumnForColumnName = "column_name";
        
        this.jdbcQueryForDatabaseNames = "SELECT table_schema FROM information_schema.tables";
        this.jdbcQueryForTableNames =    "select TABLE_NAME from INFORMATION_SCHEMA.tables where TABLE_SCHEMA='"+ this.jsqlDatabaseName +"'";
        this.jdbcQueryForColumnNames =   "select COLUMN_NAME from information_schema.columns where TABLE_SCHEMA='"+ this.jsqlDatabaseName +"' and TABLE_NAME='"+ this.jsqlTableName +"'";
        this.jdbcQueryForValues =    "select "+ this.jsqlColumnName +" from "+ this.jsqlDatabaseName +"."+ this.jsqlTableName;
        
        this.requestJdbc();
    }
    
}
