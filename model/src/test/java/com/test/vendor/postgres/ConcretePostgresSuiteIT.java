package com.test.vendor.postgres;

import com.test.AbstractTestSuite;
import org.hibernate.cfg.JdbcSettings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import spring.SpringApp;

public abstract class ConcretePostgresSuiteIT extends AbstractTestSuite {

    public ConcretePostgresSuiteIT() {
        var property = SpringApp.get("postgres");
        this.jdbcURL = property.getProperty(JdbcSettings.JAKARTA_JDBC_URL);
        this.jdbcUser = property.getProperty(JdbcSettings.JAKARTA_JDBC_USER);
        this.jdbcPass = property.getProperty(JdbcSettings.JAKARTA_JDBC_PASSWORD);

        this.databaseToInject = "public";
        this.tableToInject = "student";
        this.columnToInject = "Student_Id";
        
        this.queryAssertDatabases = "SELECT table_schema FROM information_schema.tables";
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
        this.queryAssertValues = String.format("select %s from %s.%s", this.columnToInject, this.databaseToInject, this.tableToInject);
    }

    @AfterEach
    public void checkVendor() {
        Assertions.assertEquals(
            this.injectionModel.getMediatorVendor().getPostgres(),
            this.injectionModel.getMediatorVendor().getVendor()
        );
    }
}
