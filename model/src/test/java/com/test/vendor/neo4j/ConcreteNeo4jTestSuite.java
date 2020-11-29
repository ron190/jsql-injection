package com.test.vendor.neo4j;

import com.test.AbstractTestSuite;

public abstract class ConcreteNeo4jTestSuite extends AbstractTestSuite {

    public ConcreteNeo4jTestSuite() {
        
        /*
         * Error x509 => docker-machine regenerate-certs => docker-machine restart
         * docker run --publish=7474:7474 --publish=7687:7687 neo4j
         * docker run --publish=7474:7474 --publish=7687:7687 neo4j:3.5.17
         * http://127.0.0.1:7474 => bolt://127.0.0.1 => user pwd: neo4j => pwd test
         * Virtualbox port forward 7474 7687
         */

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
}
