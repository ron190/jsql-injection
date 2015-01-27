package com.test.oracle;

import java.sql.SQLException;

import com.jsql.exception.PreparationException;
import com.test.AbstractTestSuite;

public class ConcreteOracleTestSuite extends AbstractTestSuite {
    public ConcreteOracleTestSuite () {
        DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
        USER = "system";
        PASS = "test";
        DATABASE = "HR";
        TABLE = "REGIONS";
        COLUMN = "REGION_NAME";
        
        CONF_DATABASE = "sys";
        CONF_DBNAME = "owner";
        CONF_DBTABLE = "all_tables";
        CONF_TABNAME = "table_name";
        CONF_TABTABLE = "all_tables";
        CONF_COLNAME = "column_name";
        CONF_COLTABLE = "all_tab_columns";

        try {
            initializer();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (PreparationException e) {
            e.printStackTrace();
        }
    }
}
