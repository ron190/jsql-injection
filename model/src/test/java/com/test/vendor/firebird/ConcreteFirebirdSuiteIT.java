package com.test.vendor.firebird;

import com.test.AbstractTestSuite;
import org.hibernate.cfg.JdbcSettings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import spring.SpringApp;

public abstract class ConcreteFirebirdSuiteIT extends AbstractTestSuite {

    public ConcreteFirebirdSuiteIT() {
        var property = SpringApp.get("firebird");
        this.jdbcURL = property.getProperty(JdbcSettings.JAKARTA_JDBC_URL);
        this.jdbcUser = property.getProperty(JdbcSettings.JAKARTA_JDBC_USER);
        this.jdbcPass = property.getProperty(JdbcSettings.JAKARTA_JDBC_PASSWORD);

        this.databaseToInject = "ADMIN";
        this.tableToInject = "STUDENT";
        this.columnToInject = "FIRST_NAME";
        
        this.queryAssertDatabases = "select rdb$get_context('SYSTEM', 'DB_NAME') from rdb$database";
        this.queryAssertTables = "select trim(rdb$relation_name) from rdb$relations";
        this.queryAssertColumns = String.format("""
            select trim(rdb$field_name)
            from rdb$relation_fields
            where rdb$relation_name = '%s'
        """, this.tableToInject);
        this.queryAssertValues = String.format("select %s from %s", this.columnToInject, this.tableToInject);
    }

    @AfterEach
    public void checkVendor() {
        Assertions.assertEquals(
            this.injectionModel.getMediatorVendor().getFirebird(),
            this.injectionModel.getMediatorVendor().getVendor()
        );
    }
}
