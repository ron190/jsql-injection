package com.test.mysql;

import java.sql.SQLException;

import com.jsql.exception.PreparationException;
import com.test.AbstractTestSuite;

public class ConcreteMysqlTestSuite extends AbstractTestSuite {
    public ConcreteMysqlTestSuite () {
        DB_URL = "jdbc:mysql://localhost:3306/perf-test";
        USER = "root";
        PASS = "";
        DATABASE = "perf-test";
        TABLE = "table-perf5";
        COLUMN = "libelle1";
        PROTECT = "`";
        
        CONF_DATABASE = "INFORMATION_SCHEMA";
        CONF_DBNAME = "TABLE_SCHEMA";
        CONF_DBTABLE = "tables";
        CONF_TABNAME = "TABLE_NAME";
        CONF_TABTABLE = "tables";
        CONF_COLNAME = "COLUMN_NAME";
        CONF_COLTABLE = "columns";
        
        try {
            initializer();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (PreparationException e) {
            e.printStackTrace();
        }
    }
}
