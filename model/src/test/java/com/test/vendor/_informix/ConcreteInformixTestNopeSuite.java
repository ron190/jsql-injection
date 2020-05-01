package com.test.vendor._informix;

import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;

import com.test.AbstractTestSuite;

public abstract class ConcreteInformixTestNopeSuite extends AbstractTestSuite {

    public ConcreteInformixTestNopeSuite() throws ClassNotFoundException, SQLException {
        
        this.jdbcURL = "jdbc:informix-sqli://fe80::1125:78c0:ef17:8ab5%17:7360/sysmaster:user=informix;password=test;INFORMIXSERVER=ol_informix1210_2";
        
        this.jdbcUser = StringUtils.EMPTY;
        this.jdbcPass = StringUtils.EMPTY;
        this.jsqlDatabaseName = "sysutils";
        this.jsqlTableName = "sysusers";
        this.jsqlColumnName = "username";
        
        this.jdbcColumnForDatabaseName = "name";
        this.jdbcColumnForTableName = "tabname";
        this.jdbcColumnForColumnName = "colname";
        
        this.jdbcQueryForDatabaseNames = "select distinct trim(name) name from sysmaster:sysdatabases";
        this.jdbcQueryForTableNames = "select distinct trim(tabname) tabname from sysutils:systables";
        this.jdbcQueryForColumnNames = "select distinct colname from sysutils:syscolumns c join sysutils:systables t on c.tabid = t.tabid where tabname='sysusers'";
        this.jdbcQueryForValues = "select distinct trim(username) username from sysutils:sysusers";
        
        this.requestJdbc();
    }
}
