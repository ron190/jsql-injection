package suite;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.model.bean.Column;
import com.jsql.model.bean.Database;
import com.jsql.model.bean.Table;
import com.jsql.model.injection.MediatorModel;

public abstract class AbstractTestSuite {
    @Rule
    public Retry retry = new Retry(3);
    
    // pour chaque vendor/méthode/strategy
    /**
     * liste db, table, colonne, value
     * valeur à rallonge
     * caractère spécial \
     * @throws PreparationException 
     */
 
    @BeforeClass
    public static void initialize() throws PreparationException {
        System.err.println("AbstractTestSuite and ConcreteTestSuite are for initialization only.");
        System.err.println("You should run a test suite or a unit test instead.");
        System.exit(0);
    }
    
    @Test
    public abstract void listDatabases() throws PreparationException, StoppableException; 

    @Test
    public abstract void listTables() throws PreparationException, StoppableException;
    
    @Test
    public abstract void listColumns() throws PreparationException, StoppableException;
    
    @Test
    public abstract void listValues() throws PreparationException, StoppableException;
    
    protected boolean listDatabases(List<String> list2) throws PreparationException, StoppableException {
        List<Database> dbs = MediatorModel.model().dataAccessObject.listDatabases();
        List<String> list1 = new ArrayList<String>();
        for (Database d: dbs) {
            list1.add(d.toString());
        }
        
        Set<Object> set1 = new HashSet<Object>();
        set1.addAll(list1);
        Set<Object> set2 = new HashSet<Object>();
        set2.addAll(list2);
        
        return set1.equals(set2);
    }
    
    protected boolean listTables(String table, List<String> list2) throws PreparationException, StoppableException {
        List<Table> ts = MediatorModel.model().dataAccessObject.listTables(new Database(table, "0"));
        List<String> list1 = new ArrayList<String>();
        for (Table t: ts) {
            list1.add(t.toString());
        }
        
        Set<Object> set1 = new HashSet<Object>();
        set1.addAll(list1);
        Set<Object> set2 = new HashSet<Object>();
        set2.addAll(list2);
        
        return set1.equals(set2);
    }

    protected boolean listColumns(String database, String table, List<String> list2) throws PreparationException, StoppableException {
        List<Column> ts = MediatorModel.model().dataAccessObject.listColumns(new Table(table, "0", new Database(database, "0")));
        List<String> list1 = new ArrayList<String>();
        for (Column t: ts) {
            list1.add(t.toString());
        }
        
        Set<Object> set1 = new HashSet<Object>();
        set1.addAll(list1);
        Set<Object> set2 = new HashSet<Object>();
        set2.addAll(list2);
        
        return set1.equals(set2);
    }

    protected boolean listValues(String database, String table, String column, List<String> list2) throws PreparationException, StoppableException {
        String[][] ts = MediatorModel.model().dataAccessObject.listValues(Arrays.asList(new Column(column, new Table(table, "0", new Database(database, "0")))));
        List<String> list1 = new ArrayList<String>();
        for (String[] t: ts) {
            list1.add(t[2]);
        }
        
        Set<Object> set1 = new HashSet<Object>();
        set1.addAll(list1);
        Set<Object> set2 = new HashSet<Object>();
        set2.addAll(list2);
        
        return set1.equals(set2);
    }
}
