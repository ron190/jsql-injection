package com.test;

import com.jsql.model.InjectionModel;
import com.jsql.model.bean.database.Column;
import com.jsql.model.bean.database.Database;
import com.jsql.model.bean.database.Table;
import com.jsql.model.exception.InjectionFailureException;
import com.jsql.model.exception.JSqlException;
import com.jsql.util.LogLevelUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.awaitility.Awaitility;
import org.junit.Ignore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.SpringApplication;
import spring.SpringTargetApplication;

import java.sql.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@TestInstance(Lifecycle.PER_CLASS)
@Execution(ExecutionMode.CONCURRENT)
public abstract class AbstractTestSuite {
    
    protected static final Logger LOGGER = LogManager.getRootLogger();

    private final List<String> databasesFromJdbc = new ArrayList<>();
    private final List<String> tablesFromJdbc = new ArrayList<>();
    private final List<String> columnsFromJdbc = new ArrayList<>();
    private final List<String> valuesFromJdbc = new ArrayList<>();
    
    protected String jdbcURL;
    protected String jdbcUser;
    protected String jdbcPass;
    
    protected String jdbcQueryForDatabaseNames;
    protected String jdbcQueryForTableNames;
    protected String jdbcQueryForColumnNames;
    protected String jdbcQueryForValues;
    
    protected String jdbcColumnForDatabaseName;
    protected String jdbcColumnForTableName;
    protected String jdbcColumnForColumnName;
    
    protected String jsqlDatabaseName;
    protected String jsqlTableName;
    protected String jsqlColumnName;
    
    private static final AtomicBoolean isSetupStarted = new AtomicBoolean(false);
    
    protected static final AtomicBoolean isSetupDone = new AtomicBoolean(false);
    
    protected InjectionModel injectionModel;

    public abstract void setupInjection() throws Exception;
    
    @BeforeAll
    public synchronized void initializeBackend() throws Exception {
        
        if (AbstractTestSuite.isSetupStarted.compareAndSet(false, true)) {
            
            LOGGER.info("@BeforeClass: loading Hibernate and Spring...");
            SpringTargetApplication.initializeDatabases();
            SpringApplication.run(SpringTargetApplication.class);
            
            AbstractTestSuite.isSetupDone.set(true);
        }
            
        LOGGER.info("@BeforeAll: backend is setting up...");
        Awaitility.await().atMost(Duration.ofMinutes(2)).until(AbstractTestSuite.isSetupDone::get);

        if (this.injectionModel == null) {
            
            this.requestJdbc();
            this.setupInjection();
        }
    }

    public void initialize() throws Exception {
        
        LOGGER.warn("For initialization only, run concrete test suite instead.");
        throw new InjectionFailureException();
    }

    public void requestJdbc() {
        
        try (
            Connection conn = DriverManager.getConnection(this.jdbcURL, this.jdbcUser, this.jdbcPass);
                
            Statement statementDatabase = conn.createStatement();
            ResultSet resultSetDatabase = statementDatabase.executeQuery(this.jdbcQueryForDatabaseNames);
                
            Statement statementTable = conn.createStatement();
            ResultSet resultSetTable = statementTable.executeQuery(this.jdbcQueryForTableNames);
                
            Statement statementColumn = conn.createStatement();
            ResultSet resultSetColumn = statementColumn.executeQuery(this.jdbcQueryForColumnNames);
                
            Statement statementValues = conn.createStatement();
            ResultSet resultSetValues = statementValues.executeQuery(this.jdbcQueryForValues)
        ) {
            
            while (resultSetDatabase.next()) {
                
                String dbName = resultSetDatabase.getString(this.jdbcColumnForDatabaseName);
                this.databasesFromJdbc.add(dbName);
            }
            
            while (resultSetTable.next()) {
                
                String tableName = resultSetTable.getString(this.jdbcColumnForTableName);
                this.tablesFromJdbc.add(tableName);
            }
            
            while (resultSetColumn.next()) {
                
                String colName = resultSetColumn.getString(this.jdbcColumnForColumnName);
                this.columnsFromJdbc.add(colName);
            }

            while (resultSetValues.next()) {
                
                String value = resultSetValues.getString(this.jsqlColumnName);
                this.valuesFromJdbc.add(value);
            }
            
        } catch (SQLException e) {
            
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        }
    }

    @Ignore("Enabled on inherit")
    public void listDatabases() throws JSqlException {
        
        Set<String> valuesFromInjection = new HashSet<>();
        Set<String> valuesFromJdbc = new HashSet<>();
        
        try {
            List<String> databases = this.injectionModel.getDataAccess()
                .listDatabases()
                .stream()
                .map(Database::toString)
                .collect(Collectors.toList());

            valuesFromInjection.addAll(databases);
            valuesFromJdbc.addAll(AbstractTestSuite.this.databasesFromJdbc);

            LOGGER.info("ListDatabases: found {} to find {}", valuesFromInjection, valuesFromJdbc);

            Assertions.assertTrue(
                !valuesFromInjection.isEmpty()
                && !valuesFromJdbc.isEmpty()
                && valuesFromInjection.containsAll(valuesFromJdbc)
            );
            
        } catch (AssertionError e) {
            
            Set<String> tablesUnkown = Stream.concat(
                valuesFromInjection.stream().filter(value -> !valuesFromJdbc.contains(value)),
                valuesFromJdbc.stream().filter(value -> !valuesFromInjection.contains(value))
            )
            .collect(Collectors.toCollection(TreeSet::new));
            
            throw new AssertionError(String.format("Unknown databases: %s\n%s", tablesUnkown, e));
        }
    }

    @Ignore("Enabled on inherit")
    public void listTables() throws JSqlException {
        
        Set<String> valuesFromInjection = new HashSet<>();
        Set<String> valuesFromJdbc = new HashSet<>();

        try {
            List<String> tables = this.injectionModel.getDataAccess()
                .listTables(new Database(AbstractTestSuite.this.jsqlDatabaseName, "0"))
                .stream()
                .map(Table::toString)
                .collect(Collectors.toList());

            valuesFromInjection.addAll(tables);
            valuesFromJdbc.addAll(AbstractTestSuite.this.tablesFromJdbc);

            LOGGER.info("Tables: found {} to find {}", valuesFromInjection, valuesFromJdbc);
            Assertions.assertTrue(
                !valuesFromInjection.isEmpty()
                && !valuesFromJdbc.isEmpty()
                && valuesFromInjection.equals(valuesFromJdbc)
            );
            
        } catch (AssertionError e) {
            
            Set<String> tablesUnkown = Stream.concat(
                valuesFromInjection.stream().filter(value -> !valuesFromJdbc.contains(value)),
                valuesFromJdbc.stream().filter(value -> !valuesFromInjection.contains(value))
            )
            .collect(Collectors.toCollection(TreeSet::new));
            
            throw new AssertionError(String.format("Unknown tables: %s\n%s", tablesUnkown, e));
        }
    }

    @Ignore("Enabled on inherit")
    public void listColumns() throws JSqlException {
        
        Set<String> valuesFromInjection = new HashSet<>();
        Set<String> valuesFromJdbc = new HashSet<>();

        try {
            List<String> columns = this.injectionModel.getDataAccess()
                .listColumns(
                    new Table(AbstractTestSuite.this.jsqlTableName, "0",
                        new Database(AbstractTestSuite.this.jsqlDatabaseName, "0")
                    )
                )
                .stream()
                .map(Column::toString)
                .collect(Collectors.toList());

            valuesFromInjection.addAll(columns);
            valuesFromJdbc.addAll(this.parse(AbstractTestSuite.this.columnsFromJdbc));

            LOGGER.info("listColumns: found {} to find {}", valuesFromInjection, valuesFromJdbc);
            Assertions.assertTrue(
                !valuesFromInjection.isEmpty()
                && !valuesFromJdbc.isEmpty()
                && valuesFromInjection.equals(valuesFromJdbc)
            );
            
        } catch (AssertionError e) {
            
            Set<String> columnsUnkown = Stream.concat(
                valuesFromInjection.stream().filter(value -> !valuesFromJdbc.contains(value)),
                valuesFromJdbc.stream().filter(value -> !valuesFromInjection.contains(value))
            )
            .collect(Collectors.toCollection(TreeSet::new));
            
            throw new AssertionError(String.format("Unknown columns: %s\n%s", columnsUnkown, e));
        }
    }
    
    /**
     * Default columns processing.
     * Can be overridden like for Sqlite.
     * @param columns
     * @return
     */
    protected Collection<String> parse(List<String> columns) {
        
        return columns;
    }

    @Ignore("Enabled on inherit")
    public void listValues() throws JSqlException {
        
        Set<String> valuesFromInjection = new TreeSet<>();
        Set<String> valuesFromJdbc = new TreeSet<>();

        try {
            String[][] rows = this.injectionModel.getDataAccess().listValues(List.of(
                new Column(AbstractTestSuite.this.jsqlColumnName,
                    new Table(AbstractTestSuite.this.jsqlTableName, "0",
                        new Database(AbstractTestSuite.this.jsqlDatabaseName, "0")
                    )
                )
            ));
            
            List<String> valuesFound = Arrays.stream(rows)
                // => row number, occurrence, value1, value2...
                .map(row -> row[2].replaceAll("\r\n", "\n"))
                .collect(Collectors.toList());

            valuesFromInjection.addAll(valuesFound);
            valuesFromJdbc.addAll(AbstractTestSuite.this.valuesFromJdbc);

            String logValuesFromInjection = valuesFromInjection.toString()
                .replaceAll("\n", "[n]")
                .replaceAll("\r", "[r]");
            
            String logValuesFromJdbc = valuesFromJdbc.toString()
                .replaceAll("\n", "[n]")
                .replaceAll("\r", "[r]");
            
            LOGGER.info("Values: found {} to find {}", logValuesFromInjection, logValuesFromJdbc);

            Assertions.assertTrue(
                !valuesFromInjection.isEmpty()
                && !valuesFromJdbc.isEmpty()
                // TODO update and delete injection prevent exact matching => create a specific table
                && valuesFromInjection.containsAll(valuesFromJdbc)
            );
            
        } catch (AssertionError e) {
            
            Set<String> valuesUnknown = Stream.concat(
                valuesFromInjection.stream().filter(value -> !valuesFromJdbc.contains(value)),
                valuesFromJdbc.stream().filter(value -> !valuesFromInjection.contains(value))
            )
            .collect(Collectors.toCollection(TreeSet::new));
            
            throw new AssertionError(String.format("Unknown values: %s\n%s", valuesUnknown, e));
        }
    }
}
