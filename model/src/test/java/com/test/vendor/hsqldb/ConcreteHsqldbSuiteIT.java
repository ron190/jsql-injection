package com.test.vendor.hsqldb;

import com.test.AbstractTestSuite;
import org.hibernate.cfg.JdbcSettings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import spring.SpringApp;

public abstract class ConcreteHsqldbSuiteIT extends AbstractTestSuite {

    public ConcreteHsqldbSuiteIT() {

        this.jdbcURL = SpringApp.propsHsqldb.getProperty(JdbcSettings.JAKARTA_JDBC_URL);
        this.jdbcUser = SpringApp.propsHsqldb.getProperty(JdbcSettings.JAKARTA_JDBC_USER);
        this.jdbcPass = SpringApp.propsHsqldb.getProperty(JdbcSettings.JAKARTA_JDBC_PASSWORD);

        this.jsqlDatabaseName = "PUBLIC";
        this.jsqlTableName = "STUDENT";
        this.jsqlColumnName = "STUDENT_ID";
        
        this.jdbcColumnForDatabaseName = "schema_name";
        this.jdbcColumnForTableName = "TABLE_NAME";
        this.jdbcColumnForColumnName = "COLUMN_NAME";

        this.jdbcQueryForDatabaseNames = "select distinct schema_name from INFORMATION_SCHEMA.tables t right join INFORMATION_SCHEMA.schemata s on t.TABLE_SCHEMA = s.schema_name";
        this.jdbcQueryForTableNames =    "select TABLE_NAME from information_schema.tables where TABLE_SCHEMA = '"+ this.jsqlDatabaseName +"'";
        this.jdbcQueryForColumnNames =   "select COLUMN_NAME from information_schema.columns where TABLE_SCHEMA='"+ this.jsqlDatabaseName +"' and TABLE_NAME='"+ this.jsqlTableName +"'";
        this.jdbcQueryForValues =    "select "+ this.jsqlColumnName +" from "+ this.jsqlDatabaseName +"."+ this.jsqlTableName;
    }

    @AfterEach
    public void checkVendor() {
        Assertions.assertEquals(
            this.injectionModel.getMediatorVendor().getVendor(),
            this.injectionModel.getMediatorVendor().getHsqldb()
        );
    }
}
