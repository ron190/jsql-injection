package com.test.engine.informix;

import com.test.AbstractTestSuite;
import org.hibernate.cfg.JdbcSettings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import spring.SpringApp;

public abstract class ConcreteInformixSuiteIT extends AbstractTestSuite {

    public ConcreteInformixSuiteIT() {
        var property = SpringApp.get("informix");
        this.jdbcURL = property.getProperty(JdbcSettings.JAKARTA_JDBC_URL);
        this.jdbcUser = property.getProperty(JdbcSettings.JAKARTA_JDBC_USER);
        this.jdbcPass = property.getProperty(JdbcSettings.JAKARTA_JDBC_PASSWORD);

        this.databaseToInject = "sysutils";
        this.tableToInject = "student";
        this.columnToInject = "student_id";
        
        this.queryAssertDatabases = """
            select distinct trim(name)
            from sysmaster:informix.sysdatabases
        """;
        this.queryAssertTables = String.format("""
            select distinct trim(tabname)
            from %s:informix.systables
        """, this.databaseToInject);
        this.queryAssertColumns = String.format("""
            select distinct colname
            from %s:informix.syscolumns c
            join %s:informix.systables t on c.tabid = t.tabid
            where tabname = '%s'
        """, this.databaseToInject, this.databaseToInject, this.tableToInject);
        this.queryAssertValues = String.format("select distinct %s from %s:%s", this.columnToInject, this.databaseToInject, this.tableToInject);
    }

    @AfterEach
    public void checkEngine() {
        Assertions.assertEquals(
            this.injectionModel.getMediatorEngine().getInformix(),
            this.injectionModel.getMediatorEngine().getEngine()
        );
    }
}
