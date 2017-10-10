package com.test.vendor.mysql;

import com.test.AbstractTestSuite;

public class ConcreteMysqlTestSuite extends AbstractTestSuite {

    public ConcreteMysqlTestSuite () {
        this.jdbcURL = "jdbc:mysql://"+ AbstractTestSuite.HOSTNAME +":3306/perf-test";
        this.jdbcUser = "test193746285";
        this.jdbcPass = "~Aa1";
        this.jsqlDatabaseName = "perf-test";
        this.jsqlTableName = "table-perf5";
        this.jsqlColumnName = "libelle1";
        
        this.jdbcColumnForDatabaseName = "TABLE_SCHEMA";
        this.jdbcColumnForTableName = "TABLE_NAME";
        this.jdbcColumnForColumnName = "COLUMN_NAME";
        
        this.jdbcQueryForDatabaseNames = "select TABLE_SCHEMA from INFORMATION_SCHEMA.tables where TABLE_SCHEMA='"+ this.jsqlDatabaseName +"'";
        this.jdbcQueryForTableNames =    "select TABLE_NAME from INFORMATION_SCHEMA.tables where TABLE_SCHEMA='"+ this.jsqlDatabaseName +"'";
        this.jdbcQueryForColumnNames =   "select COLUMN_NAME from information_schema.columns where TABLE_SCHEMA='"+ this.jsqlDatabaseName +"' and TABLE_NAME='"+ this.jsqlTableName +"'";
        this.jdbcQueryForValues =    "select "+ this.jsqlColumnName +" from `"+ this.jsqlDatabaseName +"`.`"+ this.jsqlTableName +"`";
        
        this.requestJdbc();
    }
    
}
