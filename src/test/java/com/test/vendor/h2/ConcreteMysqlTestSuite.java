package com.test.vendor.h2;

import com.test.AbstractTestSuite;

public class ConcreteMysqlTestSuite extends AbstractTestSuite {

    public ConcreteMysqlTestSuite () {
//        this.jdbcURL = "jdbc:mysql://"+ AbstractTestSuite.HOSTNAME +":3306/perf-test";
//        this.jdbcUser = "test193746285";
//        this.jdbcPass = "~Aa1";
//        this.jsqlDatabaseName = "perf-test";
//        this.jsqlTableName = "table-perf5";
//        this.jsqlColumnName = "libelle1";
        
//        hibernate.connection.driver_class = org.h2.Driver
//                hibernate.connection.url = jdbc:h2:mem:tenantId3;DB_CLOSE_ON_EXIT=FALSE;DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA IF NOT EXISTS MYDB1\\;CREATE TABLE IF NOT EXISTS MYDB1.TBL1 (COL1 INTEGER NOT NULL, COL2 CHAR(25)) \\;INSERT INTO MYDB1.TBL1 VALUES (1, '')\\;
//                hibernate.connection.username = sa
//                hibernate.hbm2ddl.auto = create
//                hibernate.dialect = org.hibernate.dialect.H2Dialect
        
//        this.jdbcURL = "jdbc:h2:mem:tenantId3;DB_CLOSE_ON_EXIT=FALSE;DB_CLOSE_DELAY=-1";
        this.jdbcURL = "jdbc:h2:mem:public;DB_CLOSE_ON_EXIT=FALSE;DB_CLOSE_DELAY=-1;IGNORECASE=TRUE";
        this.jdbcUser = "sa";
        this.jdbcPass = "";
        this.jsqlDatabaseName = "PUBLIC";
        this.jsqlTableName = "STUDENT";
        this.jsqlColumnName = "STUDENT_ID";
        
//        select schema_name from INFORMATION_SCHEMA.tables
//        select TABLE_NAME r from information_schema.tables where TABLE_SCHEMA = '${DATABASE}'
    
        this.jdbcColumnForDatabaseName = "TABLE_SCHEMA";
        this.jdbcColumnForTableName = "TABLE_NAME";
        this.jdbcColumnForColumnName = "COLUMN_NAME";
        
        this.jdbcQueryForDatabaseNames = "select TABLE_SCHEMA from INFORMATION_SCHEMA.tables";
        this.jdbcQueryForTableNames =    "select TABLE_NAME from information_schema.tables where TABLE_SCHEMA='"+ this.jsqlDatabaseName +"'";
        this.jdbcQueryForColumnNames =   "select COLUMN_NAME from information_schema.columns where TABLE_SCHEMA='"+ this.jsqlDatabaseName +"' and TABLE_NAME='"+ this.jsqlTableName +"'";
        this.jdbcQueryForValues =    "select "+ this.jsqlColumnName +" from `"+ this.jsqlDatabaseName +"`.`"+ this.jsqlTableName +"`";
        
        this.requestJdbc();
    }
    
}
