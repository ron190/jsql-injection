package com.test.vendor.db2;

import com.test.AbstractTestSuite;
import org.hibernate.cfg.JdbcSettings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import spring.SpringApp;

public abstract class ConcreteDb2SuiteIT extends AbstractTestSuite {

    public ConcreteDb2SuiteIT() {
        this.config();
    }
    
    public void config() {
        this.jdbcURL = SpringApp.propsDb2.getProperty(JdbcSettings.JAKARTA_JDBC_URL);
        this.jdbcUser = SpringApp.propsDb2.getProperty(JdbcSettings.JAKARTA_JDBC_USER);
        this.jdbcPass = SpringApp.propsDb2.getProperty(JdbcSettings.JAKARTA_JDBC_PASSWORD);

        this.databaseToInject = "DB2INST1";
        this.tableToInject = "STUDENT";
        this.columnToInject = "STUDENT_ID";
        
        this.queryAssertDatabases = "select trim(name) name from sysibm.sysschemata";
        this.queryAssertTables = String.format("""
            select name
            from sysibm.systables
            where creator = '%s'
        """, this.databaseToInject);
        this.queryAssertColumns = String.format("""
            select name
            from sysibm.syscolumns
            where coltype != 'BLOB'
            and tbcreator = '%s'
            and tbname = '%s'
        """, this.databaseToInject, this.tableToInject);
        this.queryAssertValues = String.format("select %s from %s.%s", this.columnToInject, this.databaseToInject, this.tableToInject);
    }

    @AfterEach
    public void checkVendor() {
        Assertions.assertEquals(
            this.injectionModel.getMediatorVendor().getDb2(),
            this.injectionModel.getMediatorVendor().getVendor()
        );
    }
}
