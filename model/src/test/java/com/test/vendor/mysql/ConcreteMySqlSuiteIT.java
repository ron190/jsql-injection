package com.test.vendor.mysql;

import com.test.AbstractTestSuite;
import org.hibernate.cfg.JdbcSettings;
import spring.SpringTargetApplication;

public abstract class ConcreteMySqlSuiteIT extends AbstractTestSuite {

    public ConcreteMySqlSuiteIT() {
        this.config();
    }
    
    public void config() {

        this.jdbcURL = SpringTargetApplication.propsMysql.getProperty(JdbcSettings.JAKARTA_JDBC_URL);
        this.jdbcUser = SpringTargetApplication.propsMysql.getProperty(JdbcSettings.JAKARTA_JDBC_USER);
        this.jdbcPass = SpringTargetApplication.propsMysql.getProperty(JdbcSettings.JAKARTA_JDBC_PASSWORD);

        this.jsqlDatabaseName = "musicstore";
        this.jsqlTableName = "Student";
        this.jsqlColumnName = "Student_Id";
        
        this.jdbcColumnForDatabaseName = "TABLE_SCHEMA";
        this.jdbcColumnForTableName = "TABLE_NAME";
        this.jdbcColumnForColumnName = "COLUMN_NAME";
        
        this.jdbcQueryForDatabaseNames = "select TABLE_SCHEMA from INFORMATION_SCHEMA.tables where TABLE_SCHEMA='"+ this.jsqlDatabaseName +"'";
        this.jdbcQueryForTableNames =    "select TABLE_NAME from INFORMATION_SCHEMA.tables where TABLE_SCHEMA='"+ this.jsqlDatabaseName +"'";
        this.jdbcQueryForColumnNames =   "select COLUMN_NAME from information_schema.columns where TABLE_SCHEMA='"+ this.jsqlDatabaseName +"' and TABLE_NAME='"+ this.jsqlTableName +"'";
        this.jdbcQueryForValues =    "select "+ this.jsqlColumnName +" from `"+ this.jsqlDatabaseName +"`.`"+ this.jsqlTableName +"`";
    }
}
