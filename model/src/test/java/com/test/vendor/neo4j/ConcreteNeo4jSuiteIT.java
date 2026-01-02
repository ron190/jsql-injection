package com.test.vendor.neo4j;

import com.test.AbstractTestSuite;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;

public abstract class ConcreteNeo4jSuiteIT extends AbstractTestSuite {

    public ConcreteNeo4jSuiteIT() {

        this.jdbcURL = "jdbc:neo4j:bolt://jsql-neo4j";
        this.jdbcUser = "neo4j";
        this.jdbcPass = "test";
        
        this.jsqlDatabaseName = "neo4j";
        this.jsqlTableName = "Movie";
        this.jsqlColumnName = "title";
        
        this.jdbcColumnForDatabaseName = "A";
        this.jdbcColumnForTableName = "label";
        this.jdbcColumnForColumnName = "d";
        
        this.jdbcQueryForDatabaseNames = "RETURN 'neo4j' AS A";
        
        this.jdbcQueryForTableNames =
             "CALL db.labels() "
            +"YIELD label      "
            +"WITH label       "
            +"RETURN   label   ";
        
        this.jdbcQueryForColumnNames =
             "MATCH (n:Movie)               "
            +"WITH collect(keys(n)) AS attr "
            +"UNWIND attr AS r              "
            +"WITH DISTINCT r[0] AS d       "
            +"ORDER BY d                    "
            +"RETURN  d                     ";
        
        this.jdbcQueryForValues =
             "MATCH (n:Movie)         "
            +"WITH DISTINCT n         "
            +"RETURN n.title as title ";
    }

    @AfterEach
    public void checkVendor() {
        Assertions.assertEquals(
            this.injectionModel.getMediatorVendor().getNeo4j(),
            this.injectionModel.getMediatorVendor().getVendor()
        );
    }
}
