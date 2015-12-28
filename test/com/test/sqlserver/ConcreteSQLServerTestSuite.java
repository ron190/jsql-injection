package com.test.sqlserver;

import java.sql.SQLException;

import com.jsql.exception.PreparationException;
import com.test.AbstractTestSuite;

public class ConcreteSQLServerTestSuite extends AbstractTestSuite {
    public ConcreteSQLServerTestSuite () {
        DB_URL = "jdbc:sqlserver://localhost:52382";
        USER = "sa";
        PASS = "test";
        TEST_DATABASE = "test";
        TEST_TABLE = "table_test_1";
        TEST_COLUMN = "test";
        
//        CONF_DATABASE = "master.";
        CONF_DBNAME = "name";
//        CONF_DBTABLE = "sysdatabases";
        CONF_TABNAME = "name";
//        CONF_TABTABLE = "sysobjects";
        CONF_COLNAME = "name";
//        CONF_COLTABLE = "syscolumns";

        SQL_DATABASES = "select name from master..sysdatabases";
        SQL_TABLES = "select name from test..sysobjects WHERE xtype='U'";   
        SQL_COLUMNS = "select c.name FROM test..syscolumns c, test..sysobjects t WHERE c.id=t.id AND t.name='table_test_1'";  
        SQL_VALUES = "select LTRIM(RTRIM(test)) test FROM test.dbo.table_test_1";   
        
        try {
            initializer();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (PreparationException e) {
            e.printStackTrace();
        }
    }
}
