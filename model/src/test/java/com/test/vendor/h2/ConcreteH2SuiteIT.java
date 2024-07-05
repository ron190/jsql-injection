package com.test.vendor.h2;

import com.test.AbstractTestSuite;
import org.hibernate.cfg.Environment;
import spring.SpringTargetApplication;

public abstract class ConcreteH2SuiteIT extends AbstractTestSuite {

    public ConcreteH2SuiteIT() {

        this.jdbcURL = SpringTargetApplication.propsH2.getProperty(Environment.URL);
        this.jdbcUser = SpringTargetApplication.propsH2.getProperty(Environment.USER);
        this.jdbcPass = SpringTargetApplication.propsH2.getProperty(Environment.PASS);

        this.jsqlDatabaseName = "PUBLIC";
        this.jsqlTableName = "STUDENT";
        this.jsqlColumnName = "STUDENT_ID";
        
        this.jdbcColumnForDatabaseName = "TABLE_SCHEMA";
        this.jdbcColumnForTableName = "TABLE_NAME";
        this.jdbcColumnForColumnName = "COLUMN_NAME";
        
        this.jdbcQueryForDatabaseNames = "select TABLE_SCHEMA from INFORMATION_SCHEMA.tables";
        this.jdbcQueryForTableNames =    "select TABLE_NAME from information_schema.tables where TABLE_SCHEMA='"+ this.jsqlDatabaseName +"'";
        this.jdbcQueryForColumnNames =   "select COLUMN_NAME from information_schema.columns where TABLE_SCHEMA='"+ this.jsqlDatabaseName +"' and TABLE_NAME='"+ this.jsqlTableName +"'";
        this.jdbcQueryForValues =    "select "+ this.jsqlColumnName +" from `"+ this.jsqlDatabaseName +"`.`"+ this.jsqlTableName +"`";
    }
}
