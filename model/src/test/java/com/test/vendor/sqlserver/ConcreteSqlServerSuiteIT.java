package com.test.vendor.sqlserver;

import com.test.AbstractTestSuite;
import org.hibernate.cfg.JdbcSettings;
import spring.SpringApp;

public abstract class ConcreteSqlServerSuiteIT extends AbstractTestSuite {

    public ConcreteSqlServerSuiteIT() {

        this.jdbcURL = SpringApp.propsSqlServer.getProperty(JdbcSettings.JAKARTA_JDBC_URL);
        this.jdbcUser = SpringApp.propsSqlServer.getProperty(JdbcSettings.JAKARTA_JDBC_USER);
        this.jdbcPass = SpringApp.propsSqlServer.getProperty(JdbcSettings.JAKARTA_JDBC_PASSWORD);

        this.jsqlDatabaseName = "master";
        this.jsqlTableName = "student";
        this.jsqlColumnName = "Student_Id";
        
        this.jdbcColumnForDatabaseName = "name";
        this.jdbcColumnForTableName = "name";
        this.jdbcColumnForColumnName = "name";
        
        this.jdbcQueryForDatabaseNames = "select name from "+ this.jsqlDatabaseName +"..sysdatabases";
        this.jdbcQueryForTableNames = "select name from "+ this.jsqlDatabaseName +"..sysobjects WHERE xtype='U'";
        this.jdbcQueryForColumnNames = "select c.name FROM "+ this.jsqlDatabaseName +"..syscolumns c, "+ this.jsqlDatabaseName +"..sysobjects t WHERE c.id=t.id AND t.name='"+ this.jsqlTableName +"'";
        this.jdbcQueryForValues = "select LTRIM(RTRIM("+ this.jsqlColumnName +")) "+ this.jsqlColumnName +" FROM "+ this.jsqlDatabaseName +".dbo."+ this.jsqlTableName;
    }
}
