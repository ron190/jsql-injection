package com.test.vendor.mckoi;

import com.test.AbstractTestSuite;

public abstract class ConcreteMckoiSuiteIT extends AbstractTestSuite {

    public ConcreteMckoiSuiteIT() {

        this.jdbcURL = "jdbc:mckoi://127.0.0.1/APP";
        this.jdbcUser = "user";
        this.jdbcPass = "password";

        this.jsqlDatabaseName = "APP";
        this.jsqlTableName = "pwn";
        this.jsqlColumnName = "dataz";

        this.jdbcColumnForDatabaseName = "name";
        this.jdbcColumnForTableName = "name";
        this.jdbcColumnForColumnName = "column";
        
        this.jdbcQueryForDatabaseNames = "select name from SYS_INFO.sUSRSchemaInfo";
        this.jdbcQueryForTableNames = "select name from SYS_INFO.sUSRTableInfo where \"schema\" = '"+ this.jsqlDatabaseName +"'";
        this.jdbcQueryForColumnNames = "select \"column\" from SYS_INFO.sUSRTableColumns where \"schema\" = '"+ this.jsqlDatabaseName +"' and \"table\" = '"+ this.jsqlTableName +"'";
        this.jdbcQueryForValues = "select "+ this.jsqlColumnName +" from "+ this.jsqlTableName;
    }
}