package com.test.vendor.neo4j;

import com.test.AbstractTestSuite;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;

public abstract class ConcreteNeo4jSuiteIT extends AbstractTestSuite {

    public ConcreteNeo4jSuiteIT() {
        this.jdbcURL = "jdbc:neo4j:bolt://jsql-neo4j";
        this.jdbcUser = "neo4j";
        this.jdbcPass = "test";
        
        this.databaseToInject = "neo4j";
        this.tableToInject = "Movie";
        this.columnToInject = "title";
        
        this.queryAssertDatabases = "RETURN 'neo4j' AS A";
        this.queryAssertTables = """
            CALL db.labels()
            YIELD label
            WITH label
            RETURN label
        """;
        this.queryAssertColumns = """
            MATCH (n:Movie)
            WITH collect(keys(n)) AS attr
            UNWIND attr AS r
            WITH DISTINCT r[0] AS d
            ORDER BY d
            RETURN d
        """;
        this.queryAssertValues = """
            MATCH (n:Movie)
            WITH DISTINCT n
            RETURN n.title as title
        """;
    }

    @AfterEach
    public void checkVendor() {
        Assertions.assertEquals(
            this.injectionModel.getMediatorVendor().getNeo4j(),
            this.injectionModel.getMediatorVendor().getVendor()
        );
    }
}
