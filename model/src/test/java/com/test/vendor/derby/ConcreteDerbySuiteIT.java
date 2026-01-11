package com.test.vendor.derby;

import com.test.AbstractTestSuite;
import org.hibernate.cfg.JdbcSettings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import spring.SpringApp;

public abstract class ConcreteDerbySuiteIT extends AbstractTestSuite {

    public ConcreteDerbySuiteIT() {
        this.jdbcURL = SpringApp.propsDerby.getProperty(JdbcSettings.JAKARTA_JDBC_URL);
        this.jdbcUser = SpringApp.propsDerby.getProperty(JdbcSettings.JAKARTA_JDBC_USER);
        this.jdbcPass = SpringApp.propsDerby.getProperty(JdbcSettings.JAKARTA_JDBC_PASSWORD);

        this.databaseToInject = "ADMIN";
        this.tableToInject = "STUDENT";
        this.columnToInject = "FIRST_NAME";
        
        this.queryAssertDatabases = "select schemaname from SYS.SYSSCHEMAS";
        this.queryAssertTables = String.format("""
            select tablename
            from sys.systables t
            inner join sys.sysschemas s on t.schemaid = s.schemaid
            where schemaname='%s'
        """, this.databaseToInject);
        this.queryAssertColumns = String.format("""
            select columnname
            from sys.systables t
            inner join sys.sysschemas s on t.schemaid = s.schemaid
            inner join sys.syscolumns c on t.tableid = c.referenceid
            where schemaname = '%s' and tablename = '%s'
            and columndatatype || '' not like 'DOUBLE%%'
            and columndatatype || '' not like 'INTEGER%%'
            and columndatatype || '' not like 'DECIMAL%%'
            and columndatatype || '' not like 'BLOB%%'
        """, this.databaseToInject, this.tableToInject);
        this.queryAssertValues = String.format("select %s from %s.%s", this.columnToInject, this.databaseToInject, this.tableToInject);
    }

    @AfterEach
    public void checkVendor() {
        Assertions.assertEquals(
            this.injectionModel.getMediatorVendor().getDerby(),
            this.injectionModel.getMediatorVendor().getVendor()
        );
    }
}
