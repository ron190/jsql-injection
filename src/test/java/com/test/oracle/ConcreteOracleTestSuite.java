package com.test.oracle;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.jsql.model.exception.InjectionFailureException;
import com.test.AbstractTestSuite;

public class ConcreteOracleTestSuite extends AbstractTestSuite {
    /**
     * Using default log4j.properties from root /
     */
    private static final Logger LOGGER = Logger.getLogger(ConcreteOracleTestSuite.class);

    public ConcreteOracleTestSuite () {
        this.jdbcURL = "jdbc:oracle:thin:@"+ AbstractTestSuite.hostName +":1521:xe";
        this.jdbcUser = "system";
        this.jdbcPassword = "test";
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
        
        try {
            initializer();
        } catch (Exception e) {
            LOGGER.warn(e);
        }
    }
}
