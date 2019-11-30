package com.test.vendor.h2;

import com.test.AbstractTestSuite;

public class ConcreteH2TestSuite extends AbstractTestSuite {

    public ConcreteH2TestSuite () {
        this.jdbcURL = "jdbc:h2:mem:public;DB_CLOSE_ON_EXIT=FALSE;DB_CLOSE_DELAY=-1;IGNORECASE=TRUE";
        this.jdbcUser = "sa";
        this.jdbcPass = "";
        this.jsqlDatabaseName = "PUBLIC";
        this.jsqlTableName = "STUDENT";
        this.jsqlColumnName = "STUDENT_ID";
        
        this.jdbcColumnForDatabaseName = "TABLE_SCHEMA";
        this.jdbcColumnForTableName = "TABLE_NAME";
        this.jdbcColumnForColumnName = "COLUMN_NAME";
        
        this.jdbcQueryForDatabaseNames = "select TABLE_SCHEMA from INFORMATION_SCHEMA.tables";
        this.jdbcQueryForTableNames =    "select TABLE_NAME from information_schema.tables where TABLE_SCHEMA='"+ this.jsqlDatabaseName +"'";
        this.jdbcQueryForColumnNames =   "select COLUMN_NAME from information_schema.columns where TABLE_SCHEMA='"+ this.jsqlDatabaseName +"' and TABLE_NAME='"+ this.jsqlTableName +"'";
        this.jdbcQueryForValues =    "select "+ this.jsqlColumnName +" from `"+ this.jsqlDatabaseName +"`.`"+ this.jsqlTableName +"`";
        
        this.requestJdbc();
    }
    
}
