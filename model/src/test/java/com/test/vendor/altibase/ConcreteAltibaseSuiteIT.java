package com.test.vendor.altibase;

import com.test.AbstractTestSuite;
import org.hibernate.cfg.JdbcSettings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import spring.SpringApp;

public abstract class ConcreteAltibaseSuiteIT extends AbstractTestSuite {

    public ConcreteAltibaseSuiteIT() {
        var property = SpringApp.get("altibase");
        this.jdbcURL = property.getProperty(JdbcSettings.JAKARTA_JDBC_URL);
        this.jdbcUser = property.getProperty(JdbcSettings.JAKARTA_JDBC_USER);
        this.jdbcPass = property.getProperty(JdbcSettings.JAKARTA_JDBC_PASSWORD);

        this.databaseToInject = "SYSTEM_";
        this.tableToInject = "SYS_DUMMY_";
        this.columnToInject = "DUMMY";
        
        this.queryAssertDatabases = "select 'system_'";
        this.queryAssertTables = "select table_name from system_.sys_tables_";
        this.queryAssertColumns = String.format("""
            select column_name
            from system_.sys_columns_ c
            inner join system_.sys_tables_ t
            on c.table_id = t.table_id
            where table_name = '%s'
        """, this.tableToInject);
        this.queryAssertValues = String.format("select %s from %s.%s", this.columnToInject, this.databaseToInject, this.tableToInject);
    }

    @AfterEach
    public void checkVendor() {
        Assertions.assertEquals(
            this.injectionModel.getMediatorVendor().getAltibase(),
            this.injectionModel.getMediatorVendor().getVendor()
        );
    }
}
