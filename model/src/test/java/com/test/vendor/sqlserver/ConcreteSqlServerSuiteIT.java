package com.test.vendor.sqlserver;

import com.test.AbstractTestSuite;
import org.hibernate.cfg.JdbcSettings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import spring.SpringApp;

public abstract class ConcreteSqlServerSuiteIT extends AbstractTestSuite {

    public ConcreteSqlServerSuiteIT() {
        this.jdbcURL = SpringApp.propsSqlServer.getProperty(JdbcSettings.JAKARTA_JDBC_URL);
        this.jdbcUser = SpringApp.propsSqlServer.getProperty(JdbcSettings.JAKARTA_JDBC_USER);
        this.jdbcPass = SpringApp.propsSqlServer.getProperty(JdbcSettings.JAKARTA_JDBC_PASSWORD);

        this.databaseToInject = "master";
        this.tableToInject = "student";
        this.columnToInject = "Student_Id";
        
        this.queryAssertDatabases = String.format("select name from %s..sysdatabases", this.databaseToInject);
        this.queryAssertTables = String.format("select name from %s..sysobjects WHERE xtype='U'", this.databaseToInject);
        this.queryAssertColumns = String.format("""
            select c.name
            FROM %s..syscolumns c, %s..sysobjects t
            WHERE c.id = t.id
            AND t.name = '%s'
        """, this.databaseToInject, this.databaseToInject, this.tableToInject);
        this.queryAssertValues = String.format("""
            select LTRIM(RTRIM(%s)) %s FROM %s.dbo.%s
        """, this.columnToInject, this.columnToInject, this.databaseToInject, this.tableToInject);
    }

    @AfterEach
    public void checkVendor() {
        Assertions.assertEquals(
            this.injectionModel.getMediatorVendor().getSqlserver(),
            this.injectionModel.getMediatorVendor().getVendor()
        );
    }
}
