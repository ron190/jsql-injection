package com.test.vendor.firebird;

import com.test.AbstractTestSuite;

public abstract class ConcreteFirebirdSuiteIT extends AbstractTestSuite {

    public ConcreteFirebirdSuiteIT() {

        this.jdbcURL = "jdbc:firebirdsql://jsql-firebird//firebird/data/EMPLOYEE.FDB?defaultHoldable";  // defaultHoldable: allows multiple queries
        this.jdbcUser = "sysdba2";
        this.jdbcPass = "test";
        this.jsqlDatabaseName = "ADMIN";
        this.jsqlTableName = "STUDENT";
        this.jsqlColumnName = "FIRST_NAME";
        
        this.jdbcColumnForDatabaseName = "rdb$get_context";
        this.jdbcColumnForTableName = "trim";  // prevent tabulation
        this.jdbcColumnForColumnName = "trim";  // prevent tabulation
        
        this.jdbcQueryForDatabaseNames = "select rdb$get_context('SYSTEM', 'DB_NAME') from rdb$database";
        this.jdbcQueryForTableNames = "select trim(rdb$relation_name) from rdb$relations";
        this.jdbcQueryForColumnNames = "select trim(rdb$field_name) from rdb$relation_fields where rdb$relation_name = '"+ this.jsqlTableName +"'";
        this.jdbcQueryForValues = "select "+ this.jsqlColumnName +" from "+ this.jsqlTableName;
    }
}
