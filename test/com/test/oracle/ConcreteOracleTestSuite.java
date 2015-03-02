package com.test.oracle;

import java.sql.SQLException;

import com.jsql.exception.PreparationException;
import com.test.AbstractTestSuite;

public class ConcreteOracleTestSuite extends AbstractTestSuite {
    public ConcreteOracleTestSuite () {
        DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
        USER = "system";
        PASS = "test";
        TEST_DATABASE = "HR";
        TEST_TABLE = "REGIONS";
        TEST_COLUMN = "REGION_NAME";
        
//        CONF_DATABASE = "sys";
        CONF_DBNAME = "owner";
//        CONF_DBTABLE = "all_tables";
        CONF_TABNAME = "table_name";
//        CONF_TABTABLE = "all_tables";
        CONF_COLNAME = "column_name";
//        CONF_COLTABLE = "all_tab_columns";

        SQL_DATABASES = "SELECT owner FROM all_tables";
        SQL_TABLES = "SELECT table_name FROM all_tables where owner='HR'";   
        SQL_COLUMNS = "SELECT column_name FROM all_tab_columns where owner='HR' and table_name='REGIONS'";  
        SQL_VALUES = "SELECT REGION_NAME FROM HR.REGIONS";   
        
        try {
            initializer();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (PreparationException e) {
            e.printStackTrace();
        }
    }
}
