package com.test.vendor.firebird;

import com.test.AbstractTestSuite;
import org.hibernate.cfg.JdbcSettings;
import spring.SpringTargetApplication;

public abstract class ConcreteFirebirdSuiteIT extends AbstractTestSuite {

    public ConcreteFirebirdSuiteIT() {

        this.jdbcURL = SpringTargetApplication.propsFirebird.getProperty(JdbcSettings.JAKARTA_JDBC_URL);
        this.jdbcUser = SpringTargetApplication.propsFirebird.getProperty(JdbcSettings.JAKARTA_JDBC_USER);
        this.jdbcPass = SpringTargetApplication.propsFirebird.getProperty(JdbcSettings.JAKARTA_JDBC_PASSWORD);

        this.jsqlDatabaseName = "ADMIN";
        this.jsqlTableName = "STUDENT";
        this.jsqlColumnName = "FIRST_NAME";
        
        this.jdbcColumnForDatabaseName = "rdb$get_context";
        this.jdbcColumnForTableName = "trim";  // prevent tabulation
        this.jdbcColumnForColumnName = "trim";  // prevent tabulation
        
        this.jdbcQueryForDatabaseNames = "select rdb$get_context('SYSTEM', 'DB_NAME') from rdb$database";
        this.jdbcQueryForTableNames = "select trim(rdb$relation_name) from rdb$relations";
        this.jdbcQueryForColumnNames = "select trim(rdb$field_name) from rdb$relation_fields where rdb$relation_name = '"+ this.jsqlTableName +"'";
        this.jdbcQueryForValues = "select "+ this.jsqlColumnName +" from "+ this.jsqlTableName;
    }
}
