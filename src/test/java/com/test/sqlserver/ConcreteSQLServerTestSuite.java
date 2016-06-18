package com.test.sqlserver;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.jsql.exception.PreparationException;
import com.test.AbstractTestSuite;

public class ConcreteSQLServerTestSuite extends AbstractTestSuite {
    /**
     * Using default log4j.properties from root /
     */
    private static final Logger LOGGER = Logger.getLogger(ConcreteSQLServerTestSuite.class);

    public ConcreteSQLServerTestSuite () {
        this.jdbcURL = "jdbc:sqlserver://localhost:52382";
        this.jdbcUser = "sa";
        this.jdbcPassword = "test";
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
        
        try {
            initializer();
        } catch (SQLException | PreparationException e) {
            LOGGER.warn(e);
        }
    }
}
