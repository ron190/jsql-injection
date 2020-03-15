package com.test.vendor.sqlserver;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.test.AbstractTestSuite;

@TestInstance(Lifecycle.PER_CLASS)
@Execution(ExecutionMode.CONCURRENT)
public abstract class ConcreteSQLServerTestSuite extends AbstractTestSuite {

    public ConcreteSQLServerTestSuite() {
        
        this.jdbcURL = "jdbc:sqlserver://127.0.0.1:3308";
        this.jdbcUser = "sa";
        this.jdbcPass = "my-secret-pw";
        this.jsqlDatabaseName = "master";
        this.jsqlTableName = "student";
        this.jsqlColumnName = "Student_Id";
        
        this.jdbcColumnForDatabaseName = "name";
        this.jdbcColumnForTableName = "name";
        this.jdbcColumnForColumnName = "name";
        
        this.jdbcQueryForDatabaseNames = "select name from "+ this.jsqlDatabaseName +"..sysdatabases";
        this.jdbcQueryForTableNames = "select name from "+ this.jsqlDatabaseName +"..sysobjects WHERE xtype='U'";
        this.jdbcQueryForColumnNames = "select c.name FROM "+ this.jsqlDatabaseName +"..syscolumns c, "+ this.jsqlDatabaseName +"..sysobjects t WHERE c.id=t.id AND t.name='"+ this.jsqlTableName +"'";
        this.jdbcQueryForValues = "select LTRIM(RTRIM("+ this.jsqlColumnName +")) "+ this.jsqlColumnName +" FROM "+ this.jsqlDatabaseName +".dbo."+ this.jsqlTableName +"";
    }
}
