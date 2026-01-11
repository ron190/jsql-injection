package com.test.vendor.mckoi;

import com.test.AbstractTestSuite;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;

public abstract class ConcreteMckoiSuiteIT extends AbstractTestSuite {

    public ConcreteMckoiSuiteIT() {
        this.jdbcURL = "jdbc:mckoi://127.0.0.1/APP";
        this.jdbcUser = "user";
        this.jdbcPass = "password";

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