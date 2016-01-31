package com.test.postgre;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.jsql.exception.PreparationException;
import com.test.AbstractTestSuite;

public class ConcretePostgreTestSuite extends AbstractTestSuite {
    /**
     * Using default log4j.properties from root /
     */
    private static final Logger LOGGER = Logger.getLogger(ConcretePostgreTestSuite.class);

    public ConcretePostgreTestSuite () {
        this.jdbcURL = "jdbc:postgresql://localhost:5432/postgres";
        this.jdbcUser = "postgres";
        this.jdbcPassword = "pg";
        this.jsqlDatabaseName = "information_schema";
        this.jsqlTableName = "sql_parts";
        this.jsqlColumnName = "feature_id";
        
        this.jdbcColumnForDatabaseName = "table_schema";
        this.jdbcColumnForTableName = "table_name";
        this.jdbcColumnForColumnName = "column_name";
        
        this.jdbcQueryForDatabaseNames = "SELECT table_schema FROM information_schema.tables";
        this.jdbcQueryForTableNames =    "select TABLE_NAME from INFORMATION_SCHEMA.tables where TABLE_SCHEMA='"+ jsqlDatabaseName +"'";   
        this.jdbcQueryForColumnNames =   "select COLUMN_NAME from information_schema.columns where TABLE_SCHEMA='"+ jsqlDatabaseName +"' and TABLE_NAME='"+ jsqlTableName +"'";  
        this.jdbcQueryForValues =    "select "+ jsqlColumnName +" from "+ jsqlDatabaseName +"."+ jsqlTableName +""; 
        
        try {
            initializer();
        } catch (SQLException e) {
            LOGGER.warn(e);
        } catch (PreparationException e) {
            LOGGER.warn(e);
        }
    }
}
