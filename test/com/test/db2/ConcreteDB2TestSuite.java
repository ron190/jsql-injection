package com.test.db2;

import java.sql.SQLException;

import com.jsql.exception.PreparationException;
import com.test.AbstractTestSuite;

public class ConcreteDB2TestSuite extends AbstractTestSuite {
    public ConcreteDB2TestSuite () {
        DB_URL = "jdbc:db2://localhost:50000/sample";
        USER = "db2admin";
        PASS = "ec3-benjo";
        TEST_DATABASE = "SYSTOOLS";
        TEST_TABLE = "POLICY";
        TEST_COLUMN = "NAME";
        
//        CONF_DATABASE = "sys";
        CONF_DBNAME = "schemaname";
//        CONF_DBTABLE = "all_tables";
        CONF_TABNAME = "name";
//        CONF_TABTABLE = "all_tables";
        CONF_COLNAME = "name";
//        CONF_COLTABLE = "all_tab_columns";

        SQL_DATABASES = "select trim(schemaname) schemaname from syscat.schemata";
        SQL_TABLES = "select trim(name) name from sysibm.systables where creator='SYSTOOLS'";   
        SQL_COLUMNS = "select trim(name) name from sysibm.syscolumns where coltype!='BLOB'and tbcreator='SYSTOOLS'and tbname='POLICY'";  
        SQL_VALUES = "SELECT trim(NAME) name FROM SYSTOOLS.POLICY";   
        
        try {
            initializer();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (PreparationException e) {
            e.printStackTrace();
        }
    }
}
