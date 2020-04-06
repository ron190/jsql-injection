package com.test.vendor.sqlite;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.test.AbstractTestSuite;

@TestInstance(Lifecycle.PER_CLASS)
@Execution(ExecutionMode.CONCURRENT)
public abstract class ConcreteSqliteTestSuite extends AbstractTestSuite {

    public ConcreteSqliteTestSuite() {
        this.config();
    }
    
    public void config() {
        
        // TODO Use same hibernate properties
        this.jdbcURL = "jdbc:sqlite:jsql-sqlite-its.db";
        this.jdbcUser = StringUtils.EMPTY;
        this.jdbcPass = StringUtils.EMPTY;
        this.jsqlDatabaseName = "musicstore";
        this.jsqlTableName = "Student";
        this.jsqlColumnName = "Student_Id";
        
        this.jdbcColumnForDatabaseName = "sqlite_master";
        this.jdbcColumnForTableName = "name";
        this.jdbcColumnForColumnName = "sql";
        
        this.jdbcQueryForDatabaseNames = "select '"+ this.jdbcColumnForDatabaseName +"' "+ this.jdbcColumnForDatabaseName +" from sqlite_master WHERE type = 'table'";
        this.jdbcQueryForTableNames =    "select "+ this.jdbcColumnForTableName +" from sqlite_master WHERE type = 'table'";
        this.jdbcQueryForColumnNames =   "select "+ this.jdbcColumnForColumnName +" from "+ this.jdbcColumnForDatabaseName +" where tbl_name = '"+ this.jsqlTableName +"' and type = 'table'";
        this.jdbcQueryForValues =    "select "+ this.jsqlColumnName +" from "+ this.jsqlTableName;
    }
}
