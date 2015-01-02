import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.model.bean.Column;
import com.jsql.model.bean.Database;
import com.jsql.model.bean.Table;
import com.jsql.model.injection.InjectionModel;
import com.jsql.model.injection.MediatorModel;
import com.jsql.model.strategy.BlindStrategy;
import com.jsql.model.strategy.ErrorbasedStrategy;
import com.jsql.model.strategy.NormalStrategy;
import com.jsql.view.println.SystemOutTerminal;


public class BasicNormalMysqlTestSuite {
    // pour chaque vendor/méthode/strategy
    /**
     * liste db, table, colonne, value
     * valeur à rallonge
     * caractère spécial \
     * @throws PreparationException 
     */
    @BeforeClass
    public static void method() throws PreparationException {
        PropertyConfigurator.configure("test/log4j.properties");
        
        InjectionModel model = new InjectionModel();
        MediatorModel.register(model);
        model.instanciationDone();
        new SystemOutTerminal();
        
        MediatorModel.model().initialUrl = "http://127.0.0.1/simulate_get.php";
        MediatorModel.model().initialUrl = "http://127.0.0.1/simulate_post.php";
        MediatorModel.model().initialUrl = "http://127.0.0.1/simulate_cookie.php";
        MediatorModel.model().initialUrl = "http://127.0.0.1/simulate_header.php";
//        MediatorModel.model().getData = "?lib=1";
//        MediatorModel.model().postData = "lib=0";
//        MediatorModel.model().postData = "lib=1";
//        MediatorModel.model().cookieData = "lib=1";
//        MediatorModel.model().cookieData = "lib=0";
        MediatorModel.model().headerData = "lib:0";
        MediatorModel.model().headerData = "lib:1";
//        MediatorModel.model().getData = "?lib=0";
        MediatorModel.model().method = "GET";
        MediatorModel.model().method = "POST";
        MediatorModel.model().method = "COOKIE";
        MediatorModel.model().method = "HEADER";
//        MediatorModel.model().initialQuery = "0+union+select+1,133727331--+";
//        MediatorModel.model().visibleIndex = "1";
//        MediatorModel.model().injectionStrategy = new NormalStrategy();
        MediatorModel.model().inputValidation();
        
        MediatorModel.model().injectionStrategy = new NormalStrategy();
        MediatorModel.model().injectionStrategy = new ErrorbasedStrategy();
        MediatorModel.model().injectionStrategy = MediatorModel.model().blindStrategy;
    }
    
    @Test
    public void listDatabases() throws PreparationException, StoppableException {
        List<Database> dbs = MediatorModel.model().dataAccessObject.listDatabases();
        List<String> list1 = new ArrayList<String>();
        for (Database d: dbs) {
            list1.add(d.toString());
        }
        
        List<String> list2 = Arrays.asList("como",
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
        
        Set<Object> set1 = new HashSet<Object>();
        set1.addAll(list1);
        Set<Object> set2 = new HashSet<Object>();
        set2.addAll(list2);
        
        Assert.assertTrue(set1.equals(set2));
    }
    
    @Test
    public void listTables() throws PreparationException, StoppableException {
        List<Table> ts = MediatorModel.model().dataAccessObject.listTables(new Database("perf-test", "0"));
        List<String> list1 = new ArrayList<String>();
        for (Table t: ts) {
            list1.add(t.toString());
        }
//        
        List<String> list2 = Arrays.asList(
                "table-perf",
                "table-perf2",
                "table-perf3",
                "table-perf5",
                "table-perf4"
                );
//        
        Set<Object> set1 = new HashSet<Object>();
        set1.addAll(list1);
        Set<Object> set2 = new HashSet<Object>();
        set2.addAll(list2);
//        
        Assert.assertTrue(set1.equals(set2));
    }
    
    @Test
    public void listColumns() throws PreparationException, StoppableException {
        List<Column> ts = MediatorModel.model().dataAccessObject.listColumns(new Table("table-perf", "0", new Database("perf-test", "0")));
        List<String> list1 = new ArrayList<String>();
        for (Column t: ts) {
            list1.add(t.toString());
        }
//        
        List<String> list2 = Arrays.asList(
                "libelle1",
                "libelle2"
                );
//        
        Set<Object> set1 = new HashSet<Object>();
        set1.addAll(list1);
        Set<Object> set2 = new HashSet<Object>();
        set2.addAll(list2);
//        
        Assert.assertTrue(set1.equals(set2));
    }
    
    @Test
    public void listValues() throws PreparationException, StoppableException {
        String[][] ts = MediatorModel.model().dataAccessObject.listValues(Arrays.asList(new Column("libelle1", new Table("table-perf5", "0", new Database("perf-test", "0")))));
        List<String> list1 = new ArrayList<String>();
        for (String[] t: ts) {
            list1.add(t[2]);
        }
//        
        List<String> list2 = Arrays.asList(
                "a",
                "b"
                );
//        
        Set<Object> set1 = new HashSet<Object>();
        set1.addAll(list1);
        Set<Object> set2 = new HashSet<Object>();
        set2.addAll(list2);
//        
        Assert.assertTrue(set1.equals(set2));
    }

}
