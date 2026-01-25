package com.test.engine.neo4j;

import com.test.AbstractTestSuite;
import org.hibernate.cfg.JdbcSettings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import spring.SpringApp;

public abstract class ConcreteNeo4jSuiteIT extends AbstractTestSuite {

    public ConcreteNeo4jSuiteIT() {
        var property = SpringApp.get("neo4j");
        this.jdbcURL = property.getProperty(JdbcSettings.JAKARTA_JDBC_URL);
        this.jdbcUser = property.getProperty(JdbcSettings.JAKARTA_JDBC_USER);
        this.jdbcPass = property.getProperty(JdbcSettings.JAKARTA_JDBC_PASSWORD);
        
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
    public void checkEngine() {
        Assertions.assertEquals(
            this.injectionModel.getMediatorEngine().getNeo4j(),
            this.injectionModel.getMediatorEngine().getEngine()
        );
    }
}
