package oracle;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import suite.AbstractTestSuite;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;

public class ConcreteOracleTestSuite extends AbstractTestSuite {
    // pour chaque vendor/méthode/strategy
    /**
     * liste db, table, colonne, value
     * valeur à rallonge
     * caractère spécial \
     * @throws PreparationException 
     */
    
    @Test
    public void listDatabases() throws PreparationException, StoppableException {
        List<String> list2 = Arrays.asList(
                "APEX_040000",
                "CTXSYS",
                "FLOWS_FILES",
                "HR",
                "MDSYS",
                "OUTLN",
                "SYS",
                "SYSTEM",
                "XDB");
        
        Assert.assertTrue(listDatabases(list2));
    }
    
    @Test
    public void listTables() throws PreparationException, StoppableException {
        List<String> list2 = Arrays.asList(
                "COUNTRIES",
                "DEPARTMENTS",
                "EMPLOYEES",
                "JOBS",
                "JOB_HISTORY",
                "LOCATIONS",
                "REGIONS");
        
        Assert.assertTrue(listTables("HR", list2));
    }
    
    @Test
    public void listColumns() throws PreparationException, StoppableException {
        List<String> list2 = Arrays.asList(
                "REGION_ID",
                "REGION_NAME"
                );
        
        Assert.assertTrue(listColumns("HR", "REGIONS", list2));
    }
    
    @Test
    public void listValues() throws PreparationException, StoppableException {
        List<String> list2 = Arrays.asList(
                "Europe",
                "Americas",
                "Asia",
                "Middle East and Africa");
        
        Assert.assertTrue(listValues("HR", "REGIONS", "REGION_NAME", list2));
    }

}
