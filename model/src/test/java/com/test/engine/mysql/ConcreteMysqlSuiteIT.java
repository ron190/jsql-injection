package com.test.engine.mysql;

import com.test.AbstractTestSuite;
import org.hibernate.cfg.JdbcSettings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import spring.SpringApp;

public abstract class ConcreteMysqlSuiteIT extends AbstractTestSuite {

    public ConcreteMysqlSuiteIT() {
        var property = SpringApp.get("mysql");
        this.jdbcURL = property.getProperty(JdbcSettings.JAKARTA_JDBC_URL);
        this.jdbcUser = property.getProperty(JdbcSettings.JAKARTA_JDBC_USER);
        this.jdbcPass = property.getProperty(JdbcSettings.JAKARTA_JDBC_PASSWORD);

        this.databaseToInject = "musicstore";
        this.tableToInject = "Student";
        this.columnToInject = "Roll_No";
        
        this.queryAssertDatabases = String.format("""
            select TABLE_SCHEMA
            from INFORMATION_SCHEMA.tables
            where TABLE_SCHEMA='%s'
        """, this.databaseToInject);
        this.queryAssertTables = String.format("""
            select TABLE_NAME
            from INFORMATION_SCHEMA.tables
            where TABLE_SCHEMA='%s'
        """, this.databaseToInject);
        this.queryAssertColumns = String.format("""
            select COLUMN_NAME
            from information_schema.columns
            where TABLE_SCHEMA='%s'
            and TABLE_NAME='%s'
        """, this.databaseToInject, this.tableToInject);
        this.queryAssertValues = String.format("select %s from `%s`.`%s`", this.columnToInject, this.databaseToInject, this.tableToInject);
    }

    @AfterEach
    public void checkEngine() {
        Assertions.assertEquals(
            this.injectionModel.getMediatorEngine().getMysql(),
            this.injectionModel.getMediatorEngine().getEngine()
        );
    }
}
