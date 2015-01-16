package com.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.PropertyConfigurator;
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
    static {
        PropertyConfigurator.configure("test/com/test/log4j.properties");
    }
    
    @Rule
    public Retry retry = new Retry(3);
    
    @BeforeClass
    public static void initialize() throws PreparationException {
        System.err.println("AbstractTestSuite and ConcreteTestSuite are for initialization purpose.");
        System.err.println("Please run a test suite or a unit test instead.");
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
    
    protected boolean compareDatabases(List<String> databases) throws PreparationException, StoppableException {
        List<Database> dbs = MediatorModel.model().dataAccessObject.listDatabases();
        List<String> databasesFound = new ArrayList<String>();
        for (Database d: dbs) {
            databasesFound.add(d.toString());
        }
        
        Set<Object> set1 = new HashSet<Object>();
        set1.addAll(databasesFound);
        Set<Object> set2 = new HashSet<Object>();
        set2.addAll(databases);
        
        return set1.equals(set2);
    }
    
    protected boolean compareTables(String table, List<String> tables) throws PreparationException, StoppableException {
        List<Table> ts = MediatorModel.model().dataAccessObject.listTables(new Database(table, "0"));
        List<String> tablesFound = new ArrayList<String>();
        for (Table t: ts) {
            tablesFound.add(t.toString());
        }
        
        Set<Object> set1 = new HashSet<Object>();
        set1.addAll(tablesFound);
        Set<Object> set2 = new HashSet<Object>();
        set2.addAll(tables);
        
        return set1.equals(set2);
    }

    protected boolean compareColumns(String database, String table, List<String> columns) throws PreparationException, StoppableException {
        List<Column> cs = MediatorModel.model().dataAccessObject.listColumns(new Table(table, "0", new Database(database, "0")));
        List<String> columnsFound = new ArrayList<String>();
        for (Column c: cs) {
            columnsFound.add(c.toString());
        }
        
        Set<Object> set1 = new HashSet<Object>();
        set1.addAll(columnsFound);
        Set<Object> set2 = new HashSet<Object>();
        set2.addAll(columns);
        
        return set1.equals(set2);
    }

    protected boolean compareValues(String database, String table, String column, List<String> values) throws PreparationException, StoppableException {
        String[][] vs = MediatorModel.model().dataAccessObject.listValues(Arrays.asList(new Column(column, new Table(table, "0", new Database(database, "0")))));
        List<String> valuesFound = new ArrayList<String>();
        for (String[] v: vs) {
            valuesFound.add(v[2]);
        }
        
        Set<Object> set1 = new HashSet<Object>();
        set1.addAll(valuesFound);
        Set<Object> set2 = new HashSet<Object>();
        set2.addAll(values);
        
        return set1.equals(set2);
    }
}
