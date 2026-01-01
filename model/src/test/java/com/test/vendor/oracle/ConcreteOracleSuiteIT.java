package com.test.vendor.oracle;

import com.test.AbstractTestSuite;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;

public abstract class ConcreteOracleSuiteIT extends AbstractTestSuite {

    public ConcreteOracleSuiteIT() {
        this.config();
    }
    
    public void config() {
        this.jdbcURL = "jdbc:oracle:thin:@jsql-oracle:1521:XE";
        this.jdbcUser = "system";
        this.jdbcPass = "Password1_One";
        this.jsqlDatabaseName = "XDB";
        this.jsqlTableName = "APP_USERS_AND_ROLES";
        this.jsqlColumnName = "NAME";
        
        this.jdbcColumnForDatabaseName = "owner";
        this.jdbcColumnForTableName = "table_name";
        this.jdbcColumnForColumnName = "column_name";
        
        this.jdbcQueryForDatabaseNames = "SELECT distinct owner FROM all_tables";
        this.jdbcQueryForTableNames = "SELECT distinct table_name FROM all_tables where owner='XDB'";
        this.jdbcQueryForColumnNames = "SELECT distinct column_name FROM all_tab_columns where owner='XDB' and table_name='APP_USERS_AND_ROLES'";
        this.jdbcQueryForValues = "SELECT distinct NAME FROM XDB.APP_USERS_AND_ROLES";
    }

    @AfterEach
    public void checkVendor() {
        Assertions.assertEquals(
            this.injectionModel.getMediatorVendor().getVendor(),
            this.injectionModel.getMediatorVendor().getOracle()
        );
    }
}
