package com.test.postgre;

import java.sql.SQLException;

import com.jsql.exception.PreparationException;
import com.test.AbstractTestSuite;

public class ConcretePostgreTestSuite extends AbstractTestSuite {
    public ConcretePostgreTestSuite () {
        DB_URL = "jdbc:postgresql://localhost:5432/postgres";
        USER = "postgres";
        PASS = "pg";
        TEST_DATABASE = "information_schema";
        TEST_TABLE = "sql_parts";
        TEST_COLUMN = "feature_id";
//        PROTECT = "`";
//        SCHEMA_OR_CATALOG = "CATALOG";
//        SYSTEM_OR_TABLE = "*";
        
//        CONF_DATABASE = "information_schema";
        CONF_DBNAME = "table_schema";
//        CONF_DBTABLE = "tables";
        CONF_TABNAME = "table_name";
//        CONF_TABTABLE = "tables";
        CONF_COLNAME = "column_name";
//        CONF_COLTABLE = "columns";
        
        SQL_DATABASES = "SELECT table_schema FROM information_schema.tables";
        SQL_TABLES =    "select TABLE_NAME from INFORMATION_SCHEMA.tables where TABLE_SCHEMA='"+ TEST_DATABASE +"'";   
        SQL_COLUMNS =   "select COLUMN_NAME from information_schema.columns where TABLE_SCHEMA='"+ TEST_DATABASE +"' and TABLE_NAME='"+ TEST_TABLE +"'";  
        SQL_VALUES =    "select "+ TEST_COLUMN +" from "+ TEST_DATABASE +"."+ TEST_TABLE +""; 
        
//        CONF_FILTERDB = "";
//        CONF_FILTERTB = "";
//        CONF_FILTERCOL = "";
//        CONF_FILTERVAL = "";
        
        try {
            initializer();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (PreparationException e) {
            e.printStackTrace();
        }
    }
}
