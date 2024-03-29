package com.test.vendor.sqlserver;

import com.test.AbstractTestSuite;

public abstract class ConcreteSqlServerSuiteIT extends AbstractTestSuite {

    public ConcreteSqlServerSuiteIT() {
        
        this.jdbcURL = "jdbc:sqlserver://jsql-sqlserver:1433;trustServerCertificate=true";
        this.jdbcUser = "sa";
        this.jdbcPass = "yourStrong(!)Password";
        this.jsqlDatabaseName = "master";
        this.jsqlTableName = "student";
        this.jsqlColumnName = "Student_Id";
        
        this.jdbcColumnForDatabaseName = "name";
        this.jdbcColumnForTableName = "name";
        this.jdbcColumnForColumnName = "name";
        
        this.jdbcQueryForDatabaseNames = "select name from "+ this.jsqlDatabaseName +"..sysdatabases";
        this.jdbcQueryForTableNames = "select name from "+ this.jsqlDatabaseName +"..sysobjects WHERE xtype='U'";
        this.jdbcQueryForColumnNames = "select c.name FROM "+ this.jsqlDatabaseName +"..syscolumns c, "+ this.jsqlDatabaseName +"..sysobjects t WHERE c.id=t.id AND t.name='"+ this.jsqlTableName +"'";
        this.jdbcQueryForValues = "select LTRIM(RTRIM("+ this.jsqlColumnName +")) "+ this.jsqlColumnName +" FROM "+ this.jsqlDatabaseName +".dbo."+ this.jsqlTableName;
    }
}
