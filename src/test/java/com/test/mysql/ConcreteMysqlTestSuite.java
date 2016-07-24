package com.test.mysql;

import org.apache.log4j.Logger;

import com.test.AbstractTestSuite;

public class ConcreteMysqlTestSuite extends AbstractTestSuite {
    /**
     * Using default log4j.properties from root /
     */
    private static final Logger LOGGER = Logger.getLogger(ConcreteMysqlTestSuite.class);

    public ConcreteMysqlTestSuite () {
        this.jdbcURL = "jdbc:mysql://"+ AbstractTestSuite.HOSTNAME +":3306/perf-test";
        this.jdbcUser = "root";
        this.jdbcPassword = "";
        this.jsqlDatabaseName = "perf-test";
        this.jsqlTableName = "table-perf5";
        this.jsqlColumnName = "libelle1";
        
        this.jdbcColumnForDatabaseName = "TABLE_SCHEMA";
        this.jdbcColumnForTableName = "TABLE_NAME";
        this.jdbcColumnForColumnName = "COLUMN_NAME";
        
        this.jdbcQueryForDatabaseNames = "select TABLE_SCHEMA from INFORMATION_SCHEMA.tables";
        this.jdbcQueryForTableNames =    "select TABLE_NAME from INFORMATION_SCHEMA.tables where TABLE_SCHEMA='"+ jsqlDatabaseName +"'";   
        this.jdbcQueryForColumnNames =   "select COLUMN_NAME from information_schema.columns where TABLE_SCHEMA='"+ jsqlDatabaseName +"' and TABLE_NAME='"+ jsqlTableName +"'";  
        this.jdbcQueryForValues =    "select "+ jsqlColumnName +" from `"+ jsqlDatabaseName +"`.`"+ jsqlTableName +"`";   
        
        try {
            initializer();
        } catch (Exception e) {
            LOGGER.warn(e);
        }
    }
}
