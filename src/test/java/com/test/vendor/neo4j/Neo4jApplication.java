package com.test.vendor.neo4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.stream.Collectors;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

public class Neo4jApplication {

    public static void main(String[] args) throws IOException, InterruptedException, SQLException {
        /*
         * Error x509 => docker-machine regenerate-certs => docker-machine restart
         * docker run --publish=7474:7474 --publish=7687:7687 neo4j
         * docker run --publish=7474:7474 --publish=7687:7687 neo4j:3.5.17
         * http://127.0.0.1:7474 => bolt://127.0.0.1 => user pwd: neo4j => pwd test
         * Virtualbox port forward 7474 7687
         */
        
        String a = Files.readAllLines(Paths.get("src/test/resources/docker/movie-graph.txt")).stream().collect(Collectors.joining("\n"));
        
        Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "test"));
        try (Session session = driver.session()) {
            Result result = session.run(a);
            result.forEachRemaining(record -> {
                System.out.println(record);
            });
        }
        driver.close();
    }

}
