package com.test.vendor.hana;

import com.test.AbstractTestSuite;
import org.hibernate.cfg.JdbcSettings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import spring.SpringApp;

public abstract class ConcreteHanaSuiteIT extends AbstractTestSuite {

    public ConcreteHanaSuiteIT() {
        this.jdbcURL = SpringApp.propsHana.getProperty(JdbcSettings.JAKARTA_JDBC_URL);
        this.jdbcUser = SpringApp.propsHana.getProperty(JdbcSettings.JAKARTA_JDBC_USER);
        this.jdbcPass = SpringApp.propsHana.getProperty(JdbcSettings.JAKARTA_JDBC_PASSWORD);

        this.databaseToInject = "SYS";
        this.tableToInject = "USERS";
        this.columnToInject = "USER_NAME";
        
        this.queryAssertDatabases = "select schema_name from sys.schemas";
        this.queryAssertTables = String.format(
            """
            select distinct name
            from sys.rs_tables_ t
            where t.schema = '%s'
            union
            select distinct name
            from sys.rs_views_ v
            where v.schema = '%s'
            """,
            this.databaseToInject,
            this.databaseToInject
        );
        this.queryAssertColumns = String.format(
            """
            select distinct c.name r
            from sys.rs_columns_ c
            inner join sys.rs_tables_ t on c.cid = t.oid
            where t.schema = '%s' and t.name = '%s'
            union
            select distinct c1.name r
            from sys.rs_columns_ c1
            inner join sys.rs_views_ v on c1.cid = v.oid
            where v.schema = '%s' and v.name = '%s'
            """,
            this.databaseToInject, this.tableToInject,
            this.databaseToInject, this.tableToInject
        );
        this.queryAssertValues = String.format("select distinct %s from %s.%s", this.columnToInject, this.databaseToInject, this.tableToInject);
    }

    @AfterEach
    public void checkVendor() {
        Assertions.assertEquals(
            this.injectionModel.getMediatorVendor().getHana(),
            this.injectionModel.getMediatorVendor().getVendor()
        );
    }
}
