package com.test.vendor.sqlserver;

import com.test.AbstractTestSuite;

public abstract class ConcreteSQLServerTestSuite extends AbstractTestSuite {

    public ConcreteSQLServerTestSuite () {
        this.jdbcURL = "jdbc:sqlserver://"+ AbstractTestSuite.HOSTNAME +":52382";
        this.jdbcUser = "sa";
        this.jdbcPass = "test";
        this.jsqlDatabaseName = "test";
        this.jsqlTableName = "table_test_1";
        this.jsqlColumnName = "test";
        
        this.jdbcColumnForDatabaseName = "name";
        this.jdbcColumnForTableName = "name";
        this.jdbcColumnForColumnName = "name";
        
        this.jdbcQueryForDatabaseNames = "select name from master..sysdatabases";
        this.jdbcQueryForTableNames = "select name from test..sysobjects WHERE xtype='U'";
        this.jdbcQueryForColumnNames = "select c.name FROM test..syscolumns c, test..sysobjects t WHERE c.id=t.id AND t.name='table_test_1'";
        this.jdbcQueryForValues = "select LTRIM(RTRIM(test)) test FROM test.dbo.table_test_1";
        
        this.requestJdbc();
    }
    
}
