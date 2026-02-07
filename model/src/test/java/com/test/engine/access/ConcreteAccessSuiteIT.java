package com.test.engine.access;

import com.test.AbstractTestSuite;
import org.hibernate.cfg.JdbcSettings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import spring.SpringApp;

import java.io.File;
import java.util.Objects;

public abstract class ConcreteAccessSuiteIT extends AbstractTestSuite {

    public ConcreteAccessSuiteIT() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("access.accdb")).getFile());

        var property = SpringApp.get("access");
        this.jdbcURL = property.getProperty(JdbcSettings.JAKARTA_JDBC_URL) + file.getAbsolutePath();
        this.jdbcUser = property.getProperty(JdbcSettings.JAKARTA_JDBC_USER);
        this.jdbcPass = property.getProperty(JdbcSettings.JAKARTA_JDBC_PASSWORD);

        this.databaseToInject = "PUBLIC";
        this.tableToInject = "TABLE1";
        this.columnToInject = "qsd";
        
        this.queryAssertDatabases = "select schema_name from information_schema.schemata";
        this.queryAssertTables = String.format("""
            select table_name
            from information_schema.tables
            where table_schema = '%s'
        """, this.databaseToInject);
        this.queryAssertColumns = String.format("""
            select column_name
            from information_schema.columns
            where table_schema = '%s'
            and table_name = '%s'
        """, this.databaseToInject, this.tableToInject);
        this.queryAssertValues = String.format("select %s from %s.%s", this.columnToInject, this.databaseToInject, this.tableToInject);
    }

    @AfterEach
    public void checkEngine() {
        Assertions.assertEquals(
            this.injectionModel.getMediatorEngine().getAccess(),
            this.injectionModel.getMediatorEngine().getEngine()
        );
    }
}
