package com.test.mysql;

import java.sql.SQLException;

import com.jsql.exception.PreparationException;
import com.test.AbstractTestSuite;

public class ConcreteMysqlTestSuite extends AbstractTestSuite {
    public ConcreteMysqlTestSuite () {
        DB_URL = "jdbc:mysql://localhost:3306/perf-test";
        USER = "root";
        PASS = "";
        TEST_DATABASE = "perf-test";
        TEST_TABLE = "table-perf5";
        TEST_COLUMN = "libelle1";
        PROTECT = "`";
        
//        CONF_DATABASE = "INFORMATION_SCHEMA";
        CONF_DBNAME = "TABLE_SCHEMA";
//        CONF_DBTABLE = "tables";
        CONF_TABNAME = "TABLE_NAME";
//        CONF_TABTABLE = "tables";
        CONF_COLNAME = "COLUMN_NAME";
//        CONF_COLTABLE = "columns";
        
        SQL_DATABASES = "select TABLE_SCHEMA from INFORMATION_SCHEMA.tables";
        SQL_TABLES =    "select TABLE_NAME from INFORMATION_SCHEMA.tables where TABLE_SCHEMA='"+ TEST_DATABASE +"'";   
        SQL_COLUMNS =   "select COLUMN_NAME from information_schema.columns where TABLE_SCHEMA='"+ TEST_DATABASE +"' and TABLE_NAME='"+ TEST_TABLE +"'";  
        SQL_VALUES =    "select "+ TEST_COLUMN +" from `"+ TEST_DATABASE +"`.`"+ TEST_TABLE +"`";   
        
        try {
            initializer();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (PreparationException e) {
            e.printStackTrace();
        }
    }
}
