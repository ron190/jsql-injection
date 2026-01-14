package com.test.vendor.presto;

import com.test.AbstractTestSuite;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;

public abstract class ConcretePrestoSuiteIT extends AbstractTestSuite {

    public ConcretePrestoSuiteIT() {
        this.jdbcURL = "jdbc:presto://jsql-presto:8084/system";
        this.jdbcUser = "test";
        this.jdbcPass = StringUtils.EMPTY;

        this.databaseToInject = "jdbc";
        this.tableToInject = "table_types";
        this.columnToInject = "table_type";
        
        this.queryAssertDatabases = "select distinct table_schema from information_schema.columns";
        this.queryAssertTables = String.format("""
            select distinct table_name
            from information_schema.columns
            where table_schema = '%s'
        """, this.databaseToInject);
        this.queryAssertColumns = String.format("""
            select distinct column_name
            from information_schema.columns
            where table_schema = '%s'
            and table_name = '%s'
        """, this.databaseToInject, this.tableToInject);
        this.queryAssertValues = String.format("select distinct %s from %s.%s", this.columnToInject, this.databaseToInject, this.tableToInject);
    }

    @AfterEach
    public void checkVendor() {
        Assertions.assertEquals(
            this.injectionModel.getMediatorVendor().getPresto(),
            this.injectionModel.getMediatorVendor().getVendor()
        );
    }
}
