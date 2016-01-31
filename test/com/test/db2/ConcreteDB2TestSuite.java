package com.test.db2;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.jsql.exception.PreparationException;
import com.test.AbstractTestSuite;

public class ConcreteDB2TestSuite extends AbstractTestSuite {
    /**
     * Using default log4j.properties from root /
     */
    private static final Logger LOGGER = Logger.getLogger(ConcreteDB2TestSuite.class);

    public ConcreteDB2TestSuite () {
        this.jdbcURL = "jdbc:db2://localhost:50000/sample";
        this.jdbcUser = "db2admin";
        this.jdbcPassword = "ec3-benjo";
        this.jsqlDatabaseName = "SYSTOOLS";
        this.jsqlTableName = "POLICY";
        this.jsqlColumnName = "NAME";
        
        this.jdbcColumnForDatabaseName = "schemaname";
        this.jdbcColumnForTableName = "name";
        this.jdbcColumnForColumnName = "name";
        
        this.jdbcQueryForDatabaseNames = "select trim(schemaname) schemaname from syscat.schemata";
        this.jdbcQueryForTableNames = "select trim(name) name from sysibm.systables where creator='SYSTOOLS'";   
        this.jdbcQueryForColumnNames = "select trim(name) name from sysibm.syscolumns where coltype!='BLOB'and tbcreator='SYSTOOLS'and tbname='POLICY'";  
        this.jdbcQueryForValues = "SELECT trim(NAME) name FROM SYSTOOLS.POLICY";   
        
        try {
            initializer();
        } catch (SQLException e) {
            LOGGER.warn(e);
        } catch (PreparationException e) {
            LOGGER.warn(e);
        }
    }
}
