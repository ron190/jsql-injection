package com.test.engine.h2;

import com.test.AbstractTestSuite;
import org.hibernate.cfg.JdbcSettings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import spring.SpringApp;

public abstract class ConcreteH2SuiteIT extends AbstractTestSuite {

    public ConcreteH2SuiteIT() {
        var property = SpringApp.get("h2");
        this.jdbcURL = property.getProperty(JdbcSettings.JAKARTA_JDBC_URL);
        this.jdbcUser = property.getProperty(JdbcSettings.JAKARTA_JDBC_USER);
        this.jdbcPass = property.getProperty(JdbcSettings.JAKARTA_JDBC_PASSWORD);

        this.databaseToInject = "PUBLIC";
        this.tableToInject = "STUDENT";
        this.columnToInject = "STUDENT_ID";
        
        this.queryAssertDatabases = "select TABLE_SCHEMA from INFORMATION_SCHEMA.tables";
        this.queryAssertTables = String.format("""
            select TABLE_NAME
            from information_schema.tables
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
            this.injectionModel.getMediatorEngine().getH2(),
            this.injectionModel.getMediatorEngine().getEngine()
        );
    }
}
