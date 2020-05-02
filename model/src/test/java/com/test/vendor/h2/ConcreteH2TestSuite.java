package com.test.vendor.h2;

import org.apache.commons.lang3.StringUtils;

import com.test.AbstractTestSuite;

public abstract class ConcreteH2TestSuite extends AbstractTestSuite {

    public ConcreteH2TestSuite() {
        
        this.jdbcURL = "jdbc:h2:tcp://127.0.0.1/mem:public;IGNORECASE=TRUE;DB_CLOSE_ON_EXIT=FALSE;DB_CLOSE_DELAY=-1;";
        this.jdbcUser = "sa";
        this.jdbcPass = StringUtils.EMPTY;
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
    }
}
