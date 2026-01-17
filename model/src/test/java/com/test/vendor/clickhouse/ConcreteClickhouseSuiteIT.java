package com.test.vendor.clickhouse;

import com.test.AbstractTestSuite;
import org.hibernate.cfg.JdbcSettings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import spring.SpringApp;

public abstract class ConcreteClickhouseSuiteIT extends AbstractTestSuite {

    public ConcreteClickhouseSuiteIT() {
        var property = SpringApp.get("clickhouse");
        this.jdbcURL = property.getProperty(JdbcSettings.JAKARTA_JDBC_URL);
        this.jdbcUser = property.getProperty(JdbcSettings.JAKARTA_JDBC_USER);
        this.jdbcPass = property.getProperty(JdbcSettings.JAKARTA_JDBC_PASSWORD);

        this.databaseToInject = "system";
        this.tableToInject = "one";
        this.columnToInject = "dummy";
        
        this.queryAssertDatabases = "select database from system.tables";
        this.queryAssertTables = String.format("""
            select name
            from system.tables
            where database = '%s'
        """, this.databaseToInject);
        this.queryAssertColumns = String.format("""
            select distinct c.name n
            from system.tables t inner join system.columns c
            on t.name = c.table
            where t.database = '%s'
            and t.name = '%s'
        """, this.databaseToInject, this.tableToInject);
        this.queryAssertValues = String.format("select %s from %s.%s", this.columnToInject, this.databaseToInject, this.tableToInject);
    }

    @AfterEach
    public void checkVendor() {
        Assertions.assertEquals(
            this.injectionModel.getMediatorVendor().getClickhouse(),
            this.injectionModel.getMediatorVendor().getVendor()
        );
    }
}
