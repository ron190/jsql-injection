package com.test.oracle;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.test.AbstractTestSuite;

public class ConcreteOracleTestSuite extends AbstractTestSuite {
    
    @Test
    public void listDatabases() throws PreparationException, StoppableException {
        List<String> databasesToFind = Arrays.asList(
            "APEX_040000",
            "CTXSYS",
            "FLOWS_FILES",
            "HR",
            "MDSYS",
            "OUTLN",
            "SYS",
            "SYSTEM",
            "XDB"
        );
        
        Assert.assertTrue(compareDatabases(databasesToFind));
    }
    
    @Test
    public void listTables() throws PreparationException, StoppableException {
        List<String> tablesToFind = Arrays.asList(
            "COUNTRIES",
            "DEPARTMENTS",
            "EMPLOYEES",
            "JOBS",
            "JOB_HISTORY",
            "LOCATIONS",
            "REGIONS"
        );
        
        Assert.assertTrue(compareTables("HR", tablesToFind));
    }
    
    @Test
    public void listColumns() throws PreparationException, StoppableException {
        List<String> columnsToFind = Arrays.asList(
            "REGION_ID",
            "REGION_NAME"
        );
        
        Assert.assertTrue(compareColumns("HR", "REGIONS", columnsToFind));
    }
    
    @Test
    public void listValues() throws PreparationException, StoppableException {
        List<String> valuesToFind = Arrays.asList(
            "Europe",
            "Americas",
            "Asia",
            "Middle East and Africa"
        );
        
        Assert.assertTrue(compareValues("HR", "REGIONS", "REGION_NAME", valuesToFind));
    }
}
