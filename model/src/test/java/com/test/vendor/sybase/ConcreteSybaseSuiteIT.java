package com.test.vendor.sybase;

import com.test.AbstractTestSuite;
import org.hibernate.cfg.JdbcSettings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import spring.SpringApp;

public abstract class ConcreteSybaseSuiteIT extends AbstractTestSuite {

    public ConcreteSybaseSuiteIT() {
        var property = SpringApp.get("sybase");
        this.jdbcURL = property.getProperty(JdbcSettings.JAKARTA_JDBC_URL);
        this.jdbcUser = property.getProperty(JdbcSettings.JAKARTA_JDBC_USER);
        this.jdbcPass = property.getProperty(JdbcSettings.JAKARTA_JDBC_PASSWORD);

        this.databaseToInject = "master";
        this.tableToInject = "Student";
        this.columnToInject = "First_Name";
        
        this.queryAssertDatabases = String.format("select distinct name from %s..sysdatabases", this.databaseToInject);
        this.queryAssertTables = String.format("select distinct name from %s..sysobjects where type = 'U'", this.databaseToInject);
        this.queryAssertColumns = String.format("""
            select distinct c.name
            from %s..syscolumns c
            inner join %s..sysobjects t on c.id = t.id
            where t.name = '%s'
        """, this.databaseToInject, this.databaseToInject, this.tableToInject);
        this.queryAssertValues = String.format("select %s from %s..%s", this.columnToInject, this.databaseToInject, this.tableToInject);
    }

    @AfterEach
    public void checkVendor() {
        Assertions.assertEquals(
            this.injectionModel.getMediatorVendor().getSybase(),
            this.injectionModel.getMediatorVendor().getVendor()
        );
    }
}
