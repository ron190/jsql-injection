package com.test.vendor.db2;

import com.test.AbstractTestSuite;

public abstract class ConcreteDB2TestSuite extends AbstractTestSuite {

    public ConcreteDB2TestSuite () {
        this.jdbcURL = "jdbc:db2://"+ AbstractTestSuite.HOSTNAME +":50000/DB2";
        this.jdbcUser = "db2admin";
        this.jdbcPass = "ec3-benjo";
        this.jsqlDatabaseName = "SYSTOOLS";
        this.jsqlTableName = "POLICY";
        this.jsqlColumnName = "NAME";
        
        this.jdbcColumnForDatabaseName = "schemaname";
        this.jdbcColumnForTableName = "name";
        this.jdbcColumnForColumnName = "name";
        
        this.jdbcQueryForDatabaseNames = "select trim("+ this.jdbcColumnForDatabaseName +") "+ this.jdbcColumnForDatabaseName +" from syscat.schemata";
        this.jdbcQueryForTableNames = "select "+ this.jdbcColumnForTableName +" from sysibm.systables where creator = '"+ this.jsqlDatabaseName +"'";
        
        this.jdbcQueryForColumnNames = ""
            + "select "
                + this.jdbcColumnForColumnName +" "
            + "from "
                + "sysibm.syscolumns "
            + "where "
                + "coltype != 'BLOB'"
                + "and tbcreator = '"+ this.jsqlDatabaseName +"'"
                + "and tbname = '"+ this.jsqlTableName +"'";
        
        this.jdbcQueryForValues = "SELECT "+ this.jsqlColumnName +" FROM "+ this.jsqlDatabaseName +"."+ this.jsqlTableName;
        
        this.requestJdbc();
    }
    
}
