package com.test.vendor.monetdb;

import com.test.AbstractTestSuite;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;

public abstract class ConcreteMonetdbSuiteIT extends AbstractTestSuite {

    public ConcreteMonetdbSuiteIT() {
        this.jdbcURL = "jdbc:monetdb://jsql-monetdb:50000/db";
        this.jdbcUser = "monetdb";
        this.jdbcPass = "monetdb";

        this.databaseToInject = "sys";
        this.tableToInject = "db_user_info";
        this.columnToInject = "name";
        
        this.queryAssertDatabases = "select name from sys.schemas";
        this.queryAssertTables = String.format("""
            select t.name
            from tables t
            inner join schemas s on t.schema_id = s.id
            where s.name = '%s'
        """, this.databaseToInject);
        this.queryAssertColumns = String.format("""
            select c.name
            from tables t
            inner join schemas s on t.schema_id = s.id
            inner join columns c on t.id = c.table_id
            where s.name = '%s' and t.name = '%s'
        """, this.databaseToInject, this.tableToInject);
        this.queryAssertValues = String.format("select %s from %s.%s", this.columnToInject, this.databaseToInject, this.tableToInject);
    }

    @AfterEach
    public void checkVendor() {
        Assertions.assertEquals(
            this.injectionModel.getMediatorVendor().getMonetdb(),
            this.injectionModel.getMediatorVendor().getVendor()
        );
    }
}
