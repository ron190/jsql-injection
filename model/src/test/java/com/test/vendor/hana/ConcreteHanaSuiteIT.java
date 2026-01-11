package com.test.vendor.hana;

import com.test.AbstractTestSuite;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;

public abstract class ConcreteHanaSuiteIT extends AbstractTestSuite {

    public ConcreteHanaSuiteIT() {

        this.jdbcURL = "jdbc:sap://127.0.0.1:39017?encrypt=false&validateCertificate=false";
        this.jdbcUser = "system";
        this.jdbcPass = "1anaHEXH";

        this.jsqlDatabaseName = "SYS";
        this.jsqlTableName = "USERS";
        this.jsqlColumnName = "USER_NAME";
        
        this.jdbcColumnForDatabaseName = "schema_name";
        this.jdbcColumnForTableName = "name";
        this.jdbcColumnForColumnName = "r";
        
        this.jdbcQueryForDatabaseNames = "select schema_name from sys.schemas";
        this.jdbcQueryForTableNames = String.format(
            """
            select distinct name
            from sys.rs_tables_ t
            where t.schema = '%s'
            union
            select distinct name
            from sys.rs_views_ v
            where v.schema = '%s'
            """,
            this.jsqlDatabaseName,
            this.jsqlDatabaseName
        );
        this.jdbcQueryForColumnNames = String.format(
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
            this.jsqlDatabaseName, this.jsqlTableName,
            this.jsqlDatabaseName, this.jsqlTableName
        );
        this.jdbcQueryForValues = "select distinct "+ this.jsqlColumnName +" from "+ this.jsqlDatabaseName +"."+ this.jsqlTableName;
    }

    @AfterEach
    public void checkVendor() {
        Assertions.assertEquals(
            this.injectionModel.getMediatorVendor().getHana(),
            this.injectionModel.getMediatorVendor().getVendor()
        );
    }
}
