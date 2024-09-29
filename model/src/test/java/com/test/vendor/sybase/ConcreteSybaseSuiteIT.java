package com.test.vendor.sybase;

import com.test.AbstractTestSuite;
import org.hibernate.cfg.JdbcSettings;
import spring.SpringTargetApplication;

public abstract class ConcreteSybaseSuiteIT extends AbstractTestSuite {

    public ConcreteSybaseSuiteIT() {

        this.jdbcURL = SpringTargetApplication.propsSybase.getProperty(JdbcSettings.JAKARTA_JDBC_URL);
        this.jdbcUser = SpringTargetApplication.propsSybase.getProperty(JdbcSettings.JAKARTA_JDBC_USER);
        this.jdbcPass = SpringTargetApplication.propsSybase.getProperty(JdbcSettings.JAKARTA_JDBC_PASSWORD);

        this.jsqlDatabaseName = "master";
        this.jsqlTableName = "Student";
        this.jsqlColumnName = "First_Name";
        
        this.jdbcColumnForDatabaseName = "name";
        this.jdbcColumnForTableName = "name";
        this.jdbcColumnForColumnName = "name";
        
        this.jdbcQueryForDatabaseNames = "select distinct name from "+ this.jsqlDatabaseName +"..sysdatabases";
        this.jdbcQueryForTableNames = "select distinct name from "+ this.jsqlDatabaseName +"..sysobjects where type = 'U'";
        this.jdbcQueryForColumnNames = "select distinct c.name from "+ this.jsqlDatabaseName +"..syscolumns c inner join "+ this.jsqlDatabaseName +"..sysobjects t on c.id = t.id where t.name = '"+ this.jsqlTableName +"'";
        this.jdbcQueryForValues = "select "+ this.jsqlColumnName +" from "+ this.jsqlDatabaseName +".."+ this.jsqlTableName;
    }
}
