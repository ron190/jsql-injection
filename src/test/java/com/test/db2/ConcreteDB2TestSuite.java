package com.test.db2;

import com.test.AbstractTestSuite;

public class ConcreteDB2TestSuite extends AbstractTestSuite {

    public ConcreteDB2TestSuite () {
        this.jdbcURL = "jdbc:db2://"+ AbstractTestSuite.HOSTNAME +":50000/sample";
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
        
        initializer();
    }
}
