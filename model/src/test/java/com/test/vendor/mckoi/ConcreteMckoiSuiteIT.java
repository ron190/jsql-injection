package com.test.vendor.mckoi;

import com.test.AbstractTestSuite;
import org.hibernate.cfg.JdbcSettings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import spring.SpringApp;

public abstract class ConcreteMckoiSuiteIT extends AbstractTestSuite {

    public ConcreteMckoiSuiteIT() {
        var property = SpringApp.get("mckoi");
        this.jdbcURL = property.getProperty(JdbcSettings.JAKARTA_JDBC_URL);
        this.jdbcUser = property.getProperty(JdbcSettings.JAKARTA_JDBC_USER);
        this.jdbcPass = property.getProperty(JdbcSettings.JAKARTA_JDBC_PASSWORD);

        this.databaseToInject = "APP";
        this.tableToInject = "pwn";
        this.columnToInject = "dataz";

        this.queryAssertDatabases = "select name from SYS_INFO.sUSRSchemaInfo";
        this.queryAssertTables = String.format("""
            select name
            from SYS_INFO.sUSRTableInfo
            where "schema" = '%s'
        """, this.databaseToInject);
        this.queryAssertColumns = String.format("""
            select "column"
            from SYS_INFO.sUSRTableColumns
            where "schema" = '%s'
            and "table" = '%s'
        """, this.databaseToInject, this.tableToInject);
        this.queryAssertValues = String.format("select %s from %s", this.columnToInject, this.tableToInject);
    }

    @AfterEach
    public void checkVendor() {
        Assertions.assertEquals(
            this.injectionModel.getMediatorVendor().getMckoi(),
            this.injectionModel.getMediatorVendor().getVendor()
        );
    }
}