package com.test.mysql;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.test.AbstractTestSuite;

public class ConcreteMysqlTestSuite extends AbstractTestSuite {
    
    @Override
    @Test
    public void listDatabases() throws PreparationException, StoppableException {
        List<String> databaseToFind = Arrays.asList(
            "como",
            "information_schema",
            "mysql",
            "perf-test",
            "performance_schema",
            "phpmyadmin",
            "test",
            "test_hibernate",
            "test_jdbc",
            "wsnguest",
            "zend-ajax",
            "zf2tutorial"
        );
        
        Assert.assertTrue(compareDatabases(databaseToFind));
    }
    
    @Override
    @Test
    public void listTables() throws PreparationException, StoppableException {
        List<String> tablesToFind = Arrays.asList(
            "table-perf",
            "table-perf2",
            "table-perf3",
            "table-perf5",
            "table-perf4"
        );
        
        Assert.assertTrue(compareTables("perf-test", tablesToFind));
    }
    
    @Override
    @Test
    public void listColumns() throws PreparationException, StoppableException {
        List<String> columnsToFind = Arrays.asList(
            "libelle1",
            "libelle2"
        );
        
        Assert.assertTrue(compareColumns("perf-test", "table-perf", columnsToFind));
    }
    
    @Override
    @Test
    public void listValues() throws PreparationException, StoppableException {
        List<String> valuesToFind = Arrays.asList(
            "a",
            "b"
        );
        
        Assert.assertTrue(compareValues("perf-test", "table-perf5", "libelle1", valuesToFind));
    }
}
