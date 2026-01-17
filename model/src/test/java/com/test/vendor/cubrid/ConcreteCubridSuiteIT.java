package com.test.vendor.cubrid;

import com.test.AbstractTestSuite;
import org.hibernate.cfg.JdbcSettings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import spring.SpringApp;

public abstract class ConcreteCubridSuiteIT extends AbstractTestSuite {

    public ConcreteCubridSuiteIT() {
        var property = SpringApp.get("cubrid");
        this.jdbcURL = property.getProperty(JdbcSettings.JAKARTA_JDBC_URL);
        this.jdbcUser = property.getProperty(JdbcSettings.JAKARTA_JDBC_USER);
        this.jdbcPass = property.getProperty(JdbcSettings.JAKARTA_JDBC_PASSWORD);

        this.databaseToInject = "PUBLIC";
        this.tableToInject = "student";
        this.columnToInject = "Student_Id";
        
        this.queryAssertDatabases = "select owner_name from db_class";
        this.queryAssertTables = String.format("""
            select class_name
            from db_class
            where owner_name='%s'
        """, this.databaseToInject);
        this.queryAssertColumns = String.format("""
            select attr_name
            from db_attribute c
            inner join db_class t on t.class_name = c.class_name
            where t.owner_name='%s'
            and t.class_name='%s'
        """, this.databaseToInject, this.tableToInject);
        this.queryAssertValues = String.format("select %s from %s", this.columnToInject, this.tableToInject);
    }

    @AfterEach
    public void checkVendor() {
        Assertions.assertEquals(
            this.injectionModel.getMediatorVendor().getCubrid(),
            this.injectionModel.getMediatorVendor().getVendor()
        );
    }
}
