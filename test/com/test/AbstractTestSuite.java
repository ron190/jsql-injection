package com.test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.PropertyConfigurator;
import org.junit.AfterClass;
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
    static {
        PropertyConfigurator.configure("test/com/test/log4j.properties");
    }

    @Rule
    public Retry retry = new Retry(3);

    @BeforeClass
    public static void initialize() throws PreparationException, SQLException {
        System.err.println("AbstractTestSuite and ConcreteTestSuite are for initialization purpose.");
        System.err.println("Please run a test suite or a unit test instead.");
        System.exit(0);
    }

    List<String> databaseToFind = new ArrayList<String>();
    List<String> tableToFind = new ArrayList<String>();
    List<String> columnToFind = new ArrayList<String>();
    List<String> valueToFind = new ArrayList<String>();

    protected String DB_URL = null;
    protected String USER = null;
    protected String PASS = null;
    protected String TEST_DATABASE = null;
    protected String TEST_TABLE = null;
    protected String TEST_COLUMN = null;
    protected String PROTECT = "";
    protected String SCHEMA_OR_CATALOG = "SCHEMA";
    protected String SYSTEM_OR_TABLE = "TABLE";
    
    protected String SQL_DATABASES = "";
    protected String SQL_TABLES = "";
    protected String SQL_COLUMNS = "";
    protected String SQL_VALUES = "";
    
    protected String CONF_DATABASE = "";
    protected String CONF_DBNAME = "";
    protected String CONF_DBTABLE = "";
    protected String CONF_TABNAME = "";
    protected String CONF_TABTABLE = "";
    protected String CONF_COLNAME = "";
    protected String CONF_COLTABLE = "";
    
    protected String CONF_FILTERDB = "";
    protected String CONF_FILTERTB = "";
    protected String CONF_FILTERCOL = "";
    protected String CONF_FILTERVAL = "";

    public void initializer() throws PreparationException, SQLException {
        Connection conn = null;
        conn = DriverManager.getConnection(DB_URL, USER, PASS);

        Statement stmt = null;
        try{
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet res = null;
            
            stmt = conn.createStatement();
//            res = stmt.executeQuery("SELECT " + CONF_DBNAME + " FROM " + CONF_DATABASE + "." + PROTECT + CONF_DBTABLE + PROTECT + " where 1=1 " + CONF_FILTERDB);
            res = stmt.executeQuery(SQL_DATABASES);
                    
            while (res.next()) {
                String tableName = res.getString(CONF_DBNAME);
                databaseToFind.add(tableName);
            }
            res.close();
            stmt.close();
            
            stmt = conn.createStatement();
//            res = stmt.executeQuery("SELECT " + CONF_TABNAME + " FROM " + CONF_DATABASE + "." + PROTECT + CONF_TABTABLE + PROTECT +
//                    " where " + CONF_DBNAME + "='" + TEST_DATABASE + "' " + CONF_FILTERTB );
            res = stmt.executeQuery(SQL_TABLES);

            while (res.next()) {
                String tableName = res.getString(CONF_TABNAME);
                tableToFind.add(tableName);
            }
            res.close();
            stmt.close();
            
            stmt = conn.createStatement();
//            res = stmt.executeQuery("SELECT " + CONF_COLNAME + " FROM " + CONF_DATABASE + "." + PROTECT + CONF_COLTABLE + PROTECT +
//                    " where " + CONF_TABNAME + "='" + TEST_TABLE + "' and " + CONF_DBNAME + "='" + TEST_DATABASE + "'" + " " + CONF_FILTERCOL );
            res = stmt.executeQuery(SQL_COLUMNS);

            while (res.next()) {
                String tableName = res.getString(CONF_COLNAME);
                columnToFind.add(tableName);
            }
            res.close();
            stmt.close();

            stmt = conn.createStatement();
//            res = stmt.executeQuery("SELECT " + TEST_COLUMN + " FROM " + PROTECT + TEST_DATABASE + PROTECT + "." + PROTECT + TEST_TABLE + PROTECT + " where 1=1 " + CONF_FILTERVAL);
            res = stmt.executeQuery(SQL_VALUES);

            while (res.next()) {
                String tableName = res.getString(TEST_COLUMN);
                valueToFind.add(tableName);
            }
            res.close();

            conn.close();
        }catch(SQLException se){
            se.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try{
                if(stmt!=null)
                    stmt.close();
            }catch(SQLException se2){
                try{
                    if(conn!=null)
                        conn.close();
                }catch(SQLException se){
                    se.printStackTrace();
                }
            }
        }
    }

    @Test
    public void listDatabases() throws PreparationException, StoppableException {
        Set<Object> set1 = new HashSet<Object>();
        Set<Object> set2 = new HashSet<Object>();
        try{
            List<Database> dbs = MediatorModel.model().dataAccessObject.listDatabases();
            List<String> databasesFound = new ArrayList<String>();
            for (Database d: dbs) {
                databasesFound.add(d.toString());
            }

            set1.addAll(databasesFound);
            set2.addAll(databaseToFind);

            System.out.println("ListDatabases: found " + set1 + "\nto find " + set2 + "\n");

            Assert.assertTrue(!set1.isEmpty() && !set2.isEmpty() && set1.equals(set2));
        }catch(AssertionError e){
            Set<Object> tmp = new TreeSet<Object>();
            for (Object x : set1)
                if (!set2.contains(x))
                    tmp.add(x);
            for (Object x : set2)
                if (!set1.contains(x))
                    tmp.add(x);
            throw new AssertionError("Error listDatabases: " + tmp + "\n" + e);
        }
    }

    @Test
    public void listTables() throws PreparationException, StoppableException {
        Set<Object> set1 = new HashSet<Object>();
        Set<Object> set2 = new HashSet<Object>();

        try{
            List<Table> ts = MediatorModel.model().dataAccessObject.listTables(new Database(TEST_DATABASE, "0"));
            List<String> tablesFound = new ArrayList<String>();
            for (Table t: ts) {
                tablesFound.add(t.toString());
            }

            set1.addAll(tablesFound);
            set2.addAll(tableToFind);

            System.out.println("listTables: found " + set1 + "\nto find " + set2 + "\n");
            Assert.assertTrue(!set1.isEmpty() && !set2.isEmpty() && set1.equals(set2));
        }catch(AssertionError e){
            Set<Object> tmp = new TreeSet<Object>();
            for (Object x : set1)
                if (!set2.contains(x))
                    tmp.add(x);
            for (Object x : set2)
                if (!set1.contains(x))
                    tmp.add(x);
            throw new AssertionError("Error listTables: " + tmp + "\n" + e);
        }
    };

    @Test
    public void listColumns() throws PreparationException, StoppableException {
        Set<Object> set1 = new HashSet<Object>();
        Set<Object> set2 = new HashSet<Object>();

        try{

            List<Column> cs = MediatorModel.model().dataAccessObject.listColumns(new Table(TEST_TABLE, "0", new Database(TEST_DATABASE, "0")));
            List<String> columnsFound = new ArrayList<String>();
            for (Column c: cs) {
                columnsFound.add(c.toString());
            }

            set1.addAll(columnsFound);
            set2.addAll(columnToFind);

            System.out.println("listColumns: found " + set1 + "\nto find " + set2 + "\n");
            Assert.assertTrue(!set1.isEmpty() && !set2.isEmpty() && set1.equals(set2));
        }catch(AssertionError e){
            Set<Object> tmp = new TreeSet<Object>();
            for (Object x : set1)
                if (!set2.contains(x))
                    tmp.add(x);
            for (Object x : set2)
                if (!set1.contains(x))
                    tmp.add(x);
            throw new AssertionError("Error listColumns: " + tmp + "\n" + e);
        }
    };

    @Test
    public void listValues() throws PreparationException, StoppableException {
        Set<Object> set1 = new HashSet<Object>();
        Set<Object> set2 = new HashSet<Object>();

        try{
            String[][] vs = MediatorModel.model().dataAccessObject.listValues(Arrays.asList(new Column(TEST_COLUMN, new Table(TEST_TABLE, "0", new Database(TEST_DATABASE, "0")))));
            List<String> valuesFound = new ArrayList<String>();
            for (String[] v: vs) {
                valuesFound.add(v[1]);
            }

            set1.addAll(valuesFound);
            set2.addAll(valueToFind);

            System.out.println("listValues: found " + set1 + "\nto find " + set2 + "\n");
            Assert.assertTrue(!set1.isEmpty() && !set2.isEmpty() && set1.equals(set2));
        }catch(AssertionError e){
            Set<Object> tmp = new TreeSet<Object>();
            for (Object x : set1)
                if (!set2.contains(x))
                    tmp.add(x);
            for (Object x : set2)
                if (!set1.contains(x))
                    tmp.add(x);
            throw new AssertionError("Error listValues: " + tmp + "\n" + e);
        }
    }
}
