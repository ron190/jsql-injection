package com.test.vendor.oracle;

import com.test.AbstractTestSuite;

public abstract class ConcreteOracleTestSuite extends AbstractTestSuite {

    public ConcreteOracleTestSuite () {
        this.jdbcURL = "jdbc:oracle:thin:@"+ AbstractTestSuite.HOSTNAME +":1521:xe";
        this.jdbcUser = "system";
        this.jdbcPass = "test";
        this.jsqlDatabaseName = "HR";
        this.jsqlTableName = "REGIONS";
        this.jsqlColumnName = "REGION_NAME";
        
        this.jdbcColumnForDatabaseName = "owner";
        this.jdbcColumnForTableName = "table_name";
        this.jdbcColumnForColumnName = "column_name";
        
        this.jdbcQueryForDatabaseNames = "SELECT owner FROM all_tables";
        this.jdbcQueryForTableNames = "SELECT table_name FROM all_tables where owner='HR'";
        this.jdbcQueryForColumnNames = "SELECT column_name FROM all_tab_columns where owner='HR' and table_name='REGIONS'";
        this.jdbcQueryForValues = "SELECT REGION_NAME FROM HR.REGIONS";
        
        this.requestJdbc();
    }
    
}
