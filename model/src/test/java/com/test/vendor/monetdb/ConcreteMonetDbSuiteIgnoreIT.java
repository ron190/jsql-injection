package com.test.vendor.monetdb;

import com.test.AbstractTestSuite;

public abstract class ConcreteMonetDbSuiteIgnoreIT extends AbstractTestSuite {

    public ConcreteMonetDbSuiteIgnoreIT() {

        this.jdbcURL = "jdbc:monetdb://jsql-monetdb:50001/db";
        this.jdbcUser = "monetdb";
        this.jdbcPass = "monetdb";

        this.jsqlDatabaseName = "sys";
        this.jsqlTableName = "db_user_info";
        this.jsqlColumnName = "name";
        
        this.jdbcColumnForDatabaseName = "name";
        this.jdbcColumnForTableName = "name";
        this.jdbcColumnForColumnName = "name";
        
        this.jdbcQueryForDatabaseNames = "select name from sys.schemas";
        this.jdbcQueryForTableNames = "select t.name from tables t inner join schemas s on t.schema_id = s.id where s.name = 'sys'";
        this.jdbcQueryForColumnNames = "select c.name from tables t inner join schemas s on t.schema_id = s.id inner join columns c on t.id = c.table_id where s.name = 'sys' and t.name = 'db_user_info'";
        this.jdbcQueryForValues = "select "+ this.jsqlColumnName +" from "+ this.jsqlDatabaseName +"."+ this.jsqlTableName;
    }
}
