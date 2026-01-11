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

        this.databaseToInject = "PUBLIC";
        this.tableToInject = "STUDENT";
        this.columnToInject = "STUDENT_ID";
        
        this.queryAssertDatabases = """
            select distinct schema_name
            from INFORMATION_SCHEMA.tables t
            right join INFORMATION_SCHEMA.schemata s on t.TABLE_SCHEMA = s.schema_name
        """;
        this.queryAssertTables = String.format("""
            select TABLE_NAME 
            from information_schema.tables 
            where TABLE_SCHEMA = '%s'
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
            this.injectionModel.getMediatorVendor().getHsqldb(),
            this.injectionModel.getMediatorVendor().getVendor()
        );
    }
}
