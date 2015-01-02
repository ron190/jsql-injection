package mysql;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import suite.AbstractTestSuite;
import suite.Retry;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.model.bean.Column;
import com.jsql.model.bean.Database;
import com.jsql.model.bean.Table;
import com.jsql.model.injection.MediatorModel;

public class ConcreteMysqlTestSuite extends AbstractTestSuite {
    // pour chaque vendor/méthode/strategy
    /**
     * liste db, table, colonne, value
     * valeur à rallonge
     * caractère spécial \
     * @throws PreparationException 
     */
//    public abstract void method() throws PreparationException;
    
    @Override
    @Test
    public void listDatabases() throws PreparationException, StoppableException {
        List<String> list2 = Arrays.asList(
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
                "zf2tutorial");
        
        Assert.assertTrue(listDatabases(list2));
    }
    
    @Override
    @Test
    public void listTables() throws PreparationException, StoppableException {
        List<String> list2 = Arrays.asList(
                "table-perf",
                "table-perf2",
                "table-perf3",
                "table-perf5",
                "table-perf4");
        
        Assert.assertTrue(listTables("perf-test", list2));
    }
    
    @Override
    @Test
    public void listColumns() throws PreparationException, StoppableException {
        List<String> list2 = Arrays.asList(
                "libelle1",
                "libelle2"
                );
        
        Assert.assertTrue(listColumns("perf-test", "table-perf", list2));
    }
    
    @Override
    @Test
    public void listValues() throws PreparationException, StoppableException {
        List<String> list2 = Arrays.asList(
                "a",
                "b");
        
        Assert.assertTrue(listValues("perf-test", "table-perf5", "libelle1", list2));
    }

}
