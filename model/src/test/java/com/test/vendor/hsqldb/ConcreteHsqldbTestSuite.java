package com.test.vendor.hsqldb;

import org.apache.commons.lang3.StringUtils;

import com.test.AbstractTestSuite;

public abstract class ConcreteHsqldbTestSuite extends AbstractTestSuite {

    public ConcreteHsqldbTestSuite() {
        
        this.jdbcURL = "jdbc:hsqldb:hsql://127.0.0.1:9002/mainDb";
        this.jdbcUser = "sa";
        this.jdbcPass = StringUtils.EMPTY;
        this.jsqlDatabaseName = "PUBLIC";
        this.jsqlTableName = "STUDENT";
        this.jsqlColumnName = "STUDENT_ID";
        
        this.jdbcColumnForDatabaseName = "schema_name";
        this.jdbcColumnForTableName = "TABLE_NAME";
        this.jdbcColumnForColumnName = "COLUMN_NAME";
        
        
        this.jdbcQueryForDatabaseNames = "select distinct schema_name from INFORMATION_SCHEMA.tables t right join INFORMATION_SCHEMA.schemata s on t.TABLE_SCHEMA = s.schema_name";
        this.jdbcQueryForTableNames =    "select TABLE_NAME from information_schema.tables where TABLE_SCHEMA = '"+ this.jsqlDatabaseName +"'";
        this.jdbcQueryForColumnNames =   "select COLUMN_NAME from information_schema.columns where TABLE_SCHEMA='"+ this.jsqlDatabaseName +"' and TABLE_NAME='"+ this.jsqlTableName +"'";
        this.jdbcQueryForValues =    "select "+ this.jsqlColumnName +" from "+ this.jsqlDatabaseName +"."+ this.jsqlTableName +"";
    }
}
