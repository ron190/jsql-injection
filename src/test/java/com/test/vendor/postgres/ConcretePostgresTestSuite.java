package com.test.vendor.postgres;

import com.test.AbstractTestSuite;

public class ConcretePostgresTestSuite extends AbstractTestSuite {

    public ConcretePostgresTestSuite () {
        this.config();
        this.requestJdbc();
    }
    
    public void config() {
        this.jdbcURL = "jdbc:postgresql://127.0.0.1:5432/";
        this.jdbcUser = "postgres";
        this.jdbcPass = "mysecretpassword";
        this.jsqlDatabaseName = "public";
        this.jsqlTableName = "student";
        this.jsqlColumnName = "Student_Id";
        
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
