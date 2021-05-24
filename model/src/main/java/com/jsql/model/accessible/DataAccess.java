/*******************************************************************************
 * Copyhacked (H) 2012-2020.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.model.accessible;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.model.bean.database.AbstractElementDatabase;
import com.jsql.model.bean.database.Column;
import com.jsql.model.bean.database.Database;
import com.jsql.model.bean.database.Table;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.model.exception.AbstractSlidingException;
import com.jsql.model.exception.InjectionFailureException;
import com.jsql.model.exception.JSqlException;
import com.jsql.model.suspendable.SuspendableGetRows;
import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevel;

/**
 * Database resource object to read name of databases, tables, columns and values
 * using most suited injection strategy.
 */
public class DataAccess {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    /**
     * SQL characters marking the end of the result of an injection.
     * Process stops when this schema is encountered:
     * <pre>SQLix01x03x03x07
     */
    public static final String LEAD_HEX = "0x53714c69";
    public static final String TRAIL_SQL = "%01%03%03%07";
    public static final String TRAIL_HEX = "0x01030307";
    
    /**
     * Regex characters marking the end of the result of an injection.
     * Process stops when this schema is encountered:
     * <pre>SQLix01x03x03x07
     */
    public static final String TRAIL_RGX = "\\x01\\x03\\x03\\x07";
    
    public static final String SEPARATOR_FIELD_HEX = "0x7f";
    public static final String SEPARATOR_FIELD_SQL = "%7f";
    
    /**
     * Regex character used between each table cells.
     * Expected schema of multiple table cells :
     * <pre>
     * x04[table cell]x05[number of occurrences]x04x06x04[table cell]x05[number of occurrences]x04
     */
    public static final String SEPARATOR_CELL_RGX = "\\x06";
    
    /**
     * SQL character used between each table cells.
     * Expected schema of multiple table cells :
     * <pre>
     * %04[table cell]%05[number of occurrences]%04%06%04[table cell]%05[number of occurrences]%04
     */
    public static final String SEPARATOR_CELL_SQL = "%06";
    public static final String SEPARATOR_CELL_HEX = "0x06";
    
    /**
     * SQL character used between the table cell and the number of occurrence of the cell text.
     * Expected schema of a table cell data is
     * <pre>%04[table cell]%05[number of occurrences]%04
     */
    public static final String SEPARATOR_QTE_SQL = "%05";
    
    /**
     * Regex character used between the table cell and the number of occurrence of the cell text.
     * Expected schema of a table cell data is
     * <pre>x04[table cell]x05[number of occurrences]x04
     */
    public static final String SEPARATOR_QTE_RGX = "\\x05";
    public static final String SEPARATOR_QTE_HEX = "0x05";
    
    /**
     * Regex character enclosing a table cell returned by injection.
     * It allows to detect the correct end of a table cell data during parsing.
     * Expected schema of a table cell data is
     * <pre>x04[table cell]x05[number of occurrences]x04
     */
    public static final String ENCLOSE_VALUE_RGX = "\\x04";
    public static final String ENCLOSE_VALUE_HEX = "0x04";
    
    /**
     * SQL character enclosing a table cell returned by injection.
     * It allows to detect the correct end of a table cell data during parsing.
     * Expected schema of a table cell data is
     * <pre>%04[table cell]%05[number of occurrences]%04
     */
    public static final String ENCLOSE_VALUE_SQL = "%04";
    
    public static final String CALIBRATOR_SQL = "%23";
    public static final String CALIBRATOR_HEX = "0x23";
    
    public static final String LEAD = "SqLi";
    public static final String SHELL_LEAD = "${shell.lead}";
    public static final String TRAIL = "iLQS";
    public static final String SHELL_TRAIL = "${shell.trail}";
    
    /**
     * Regex keywords corresponding to multiline and case insensitive match.
     */
    public static final String MODE = "(?si)";
    
    /**
     * Regex schema describing a table cell with firstly the cell content and secondly the number of occurrences
     * of the cell text, separated by the reserved character x05 in hexadecimal.
     * The range of characters from x01 to x1F are not printable ASCII characters used to parse the data and exclude
     * printable characters during parsing.
     * Expected schema of a table cell data is
     * <pre>x04[table cell]x05[number of occurrences]x04
     */
    public static final String CELL_TABLE = "([^\\x01-\\x09\\x0B-\\x0C\\x0E-\\x1F]*)"+ SEPARATOR_QTE_RGX +"([^\\x01-\\x09\\x0B-\\x0C\\x0E-\\x1F]*)(\\x08)?";
    
    private InjectionModel injectionModel;
    
    public DataAccess(InjectionModel injectionModel) {
        
        this.injectionModel = injectionModel;
    }
    
    /**
     * Get general database informations.<br>
     * => version{%}database{%}user{%}CURRENT_USER
     * @throws JSqlException
     */
    public void getDatabaseInfos() throws JSqlException {
        
        LOGGER.log(LogLevel.CONSOLE_DEFAULT, () -> I18nUtil.valueByKey("LOG_FETCHING_INFORMATIONS"));
        
        var sourcePage = new String[]{ StringUtils.EMPTY };

        var resultToParse = "";
        
        try {
            resultToParse = new SuspendableGetRows(this.injectionModel).run(
                this.injectionModel.getMediatorVendor().getVendor().instance().sqlInfos(),
                sourcePage,
                false,
                0,
                null,
                "metadata"
            );
        
        } catch (AbstractSlidingException e) {
            
            LOGGER.log(LogLevel.CONSOLE_ERROR, e.getMessage());
            
            // Get pieces of data already retrieved instead of losing them
            if (StringUtils.isNotEmpty(e.getSlidingWindowAllRows())) {
                
                resultToParse = e.getSlidingWindowAllRows();
                
            } else if (StringUtils.isNotEmpty(e.getSlidingWindowCurrentRows())) {
                
                resultToParse = e.getSlidingWindowCurrentRows();
            }
            
        } catch (Exception e) {
            
            // TODO Check if useful
            // Catch every other exception (timeout?)
            LOGGER.log(LogLevel.CONSOLE_ERROR, e.getMessage(), e);
        }

        if (StringUtils.isEmpty(resultToParse)) {
            
            this.injectionModel.sendResponseFromSite("Incorrect metadata", sourcePage[0].trim());
        }
        
        try {
            String versionDatabase = resultToParse.split(ENCLOSE_VALUE_RGX)[0].replaceAll("\\s+", StringUtils.SPACE);
            String nameDatabase = resultToParse.split(ENCLOSE_VALUE_RGX)[1];
            String username = resultToParse.split(ENCLOSE_VALUE_RGX)[2];
            
            var infos = String
                .format(
                    "Database [%s] on %s [%s] for user [%s]",
                    nameDatabase,
                    this.injectionModel.getMediatorVendor().getVendor(),
                    versionDatabase,
                    username
                );
            
            LOGGER.log(LogLevel.CONSOLE_SUCCESS, infos);
            
        } catch (ArrayIndexOutOfBoundsException e) {

            LOGGER.log(
                LogLevel.CONSOLE_ERROR,
                String.format("%s: %s", I18nUtil.valueByKey("LOG_DB_METADATA_INCORRECT"), resultToParse),
                e
            );
            LOGGER.log(LogLevel.CONSOLE_INFORM, I18nUtil.valueByKey("LOG_DB_METADATA_WARN"));
        }
    }
    
    /**
     * Get database names and table counts and send them to the view.<br>
     * Use readable text (not hexa) and parse this pattern:<br>
     * => hh[database name 1]jj[table count]hhgghh[database name 2]jj[table count]hhggh...hi<br>
     * Data window can be cut before the end of the request but the process helps to obtain
     * the rest of the unreachable data. The process can be interrupted by the user (stop/pause).
     * @return list of databases found
     * @throws JSqlException when injection failure or stopped by user
     */
    public List<Database> listDatabases() throws JSqlException {
        
        LOGGER.log(LogLevel.CONSOLE_DEFAULT, () -> I18nUtil.valueByKey("LOG_FETCHING_DATABASES"));
        
        List<Database> databases = new ArrayList<>();
        
        String resultToParse = StringUtils.EMPTY;
        
        try {
            var sourcePage = new String[]{ StringUtils.EMPTY };
            resultToParse = new SuspendableGetRows(this.injectionModel).run(
                this.injectionModel.getMediatorVendor().getVendor().instance().sqlDatabases(),
                sourcePage,
                true,
                0,
                null,
                "databases"
            );
            
        } catch (AbstractSlidingException e) {
            
            LOGGER.log(LogLevel.CONSOLE_ERROR, e.getMessage());
            
            // Get pieces of data already retrieved instead of losing them
            if (StringUtils.isNotEmpty(e.getSlidingWindowAllRows())) {
                
                resultToParse = e.getSlidingWindowAllRows();
                
            } else if (StringUtils.isNotEmpty(e.getSlidingWindowCurrentRows())) {
                
                resultToParse = e.getSlidingWindowCurrentRows();
            }
            
        } catch (Exception e) {
            
            // TODO Check if useful
            // Catch every other exception (timeout?)
            LOGGER.log(LogLevel.CONSOLE_ERROR, e.getMessage(), e);
        }

        // Parse all data we have retrieved
        var regexSearch = Pattern
            .compile(
                MODE
                + ENCLOSE_VALUE_RGX
                + CELL_TABLE
                + ENCLOSE_VALUE_RGX
            )
            .matcher(resultToParse);

        if (!regexSearch.find()) {
            
            throw new InjectionFailureException("No match while injecting databases");
        }
        
        regexSearch.reset();

        // Build an array of Database objects from the data we have parsed
        while (regexSearch.find()) {
            
            String databaseName = regexSearch.group(1);
            String tableCount = regexSearch.group(2);

            var newDatabase = new Database(databaseName, tableCount);
            databases.add(newDatabase);
        }

        var request = new Request();
        request.setMessage(Interaction.ADD_DATABASES);
        request.setParameters(databases);
        this.injectionModel.sendToViews(request);
        
        return databases;
    }

    /**
     * Get tables name and row count and send them to the view.<br>
     * Use readable text (not hexa) and parse this pattern:<br>
     * => hh[table name 1]jj[rows count]hhgghh[table name 2]jj[rows count]hhggh...hi<br>
     * Data window can be cut before the end of the request but the process helps to obtain
     * the rest of the unreachable data. The process can be interrupted by the user (stop/pause).
     * @param database which contains tables to find
     * @return list of tables found
     * @throws JSqlException when injection failure or stopped by user
     */
    public List<Table> listTables(Database database) throws JSqlException {
        
        // Reset stoppedByUser if list of Databases is partial
        // and some Tables are still reachable
        this.injectionModel.setIsStoppedByUser(false);
        
        // Inform the view that database has just been used
        var requestStartProgress = new Request();
        requestStartProgress.setMessage(Interaction.START_PROGRESS);
        requestStartProgress.setParameters(database);
        this.injectionModel.sendToViews(requestStartProgress);

        var tableCount = Integer.toString(database.getChildCount());
        
        String resultToParse = StringUtils.EMPTY;
        
        try {
            var pageSource = new String[]{ StringUtils.EMPTY };
            resultToParse = new SuspendableGetRows(this.injectionModel).run(
                this.injectionModel.getMediatorVendor().getVendor().instance().sqlTables(database),
                pageSource,
                true,
                Integer.parseInt(tableCount),
                database,
                "tables"
            );
            
        } catch (AbstractSlidingException e) {
            
            LOGGER.log(LogLevel.CONSOLE_ERROR, e.getMessage());
            
            // Get pieces of data already retrieved instead of losing them
            if (StringUtils.isNotEmpty(e.getSlidingWindowAllRows())) {
                
                resultToParse = e.getSlidingWindowAllRows();
                
            } else if (StringUtils.isNotEmpty(e.getSlidingWindowCurrentRows())) {
                
                resultToParse = e.getSlidingWindowCurrentRows();
            }
            
        } catch (Exception e) {
            
            // TODO Check if useful
            // Catch every other exception (timeout?)
            LOGGER.log(LogLevel.CONSOLE_ERROR, e.getMessage(), e);
        }

        // Parse all the data we have retrieved
        var regexSearch = Pattern
            .compile(
                MODE
                + ENCLOSE_VALUE_RGX
                + CELL_TABLE
                + ENCLOSE_VALUE_RGX
            )
            .matcher(resultToParse);
        
        var requestEndProgress = new Request();
        requestEndProgress.setMessage(Interaction.END_PROGRESS);
        requestEndProgress.setParameters(database);
        this.injectionModel.sendToViews(requestEndProgress);
        
        if (!regexSearch.find()) {
            
            throw new InjectionFailureException("No match while injecting tables");
        }
        
        regexSearch.reset();
        
        List<Table> tables = new ArrayList<>();
        
        // Build an array of Table objects from the data we have parsed
        while (regexSearch.find()) {
            
            String tableName = regexSearch.group(1);
            String rowCount = regexSearch.group(2);
            
            var newTable = new Table(tableName, rowCount, database);
            tables.add(newTable);
        }
        
        var requestAddTables = new Request();
        requestAddTables.setMessage(Interaction.ADD_TABLES);
        requestAddTables.setParameters(tables);
        this.injectionModel.sendToViews(requestAddTables);
        
        return tables;
    }

    /**
     * Get column names and send them to the view.<br>
     * Use readable text (not hexa) and parse this pattern with 2nd member forced to 31 (1 in ascii):<br>
     * => hh[column name 1]jj[31]hhgghh[column name 2]jj[31]hhggh...hi<br>
     * Data window can be cut before the end of the request but the process helps to obtain
     * the rest of the unreachable data. The process can be interrupted by the user (stop/pause).
     * @param table which contains columns to find
     * @return list of columns found
     * @throws JSqlException when injection failure or stopped by user
     */
    public List<Column> listColumns(Table table) throws JSqlException {
        
        List<Column> columns = new ArrayList<>();
        
        // Inform the view that table has just been used
        var requestStartProgress = new Request();
        requestStartProgress.setMessage(Interaction.START_INDETERMINATE_PROGRESS);
        requestStartProgress.setParameters(table);
        this.injectionModel.sendToViews(requestStartProgress);

        String resultToParse = StringUtils.EMPTY;
        
        try {
            var pageSource = new String[]{ StringUtils.EMPTY };
            resultToParse = new SuspendableGetRows(this.injectionModel).run(
                this.injectionModel.getMediatorVendor().getVendor().instance().sqlColumns(table),
                pageSource,
                true,
                0,
                table,
                "columns"
            );
            
        } catch (AbstractSlidingException e) {
            
            LOGGER.log(LogLevel.CONSOLE_ERROR, e.getMessage());
            
            // Get pieces of data already retrieved instead of losing them
            if (StringUtils.isNotEmpty(e.getSlidingWindowAllRows())) {
                
                resultToParse = e.getSlidingWindowAllRows();
                
            } else if (StringUtils.isNotEmpty(e.getSlidingWindowCurrentRows())) {
                
                resultToParse = e.getSlidingWindowCurrentRows();
            }
            
        } catch (Exception e) {
            
            // TODO Check if useful
            // Catch every other exception (timeout?)
            LOGGER.log(LogLevel.CONSOLE_ERROR, e.getMessage(), e);
        }

        // Build SQLite columns
        if (this.injectionModel.getMediatorVendor().isSqlite()) {
            
            resultToParse = this.injectionModel.getMediatorVendor().getSqlite().transformSqlite(resultToParse);
        }
        
        // Parse all the data we have retrieved
        var regexSearch = Pattern
            .compile(
                MODE
                + ENCLOSE_VALUE_RGX
                + CELL_TABLE
                + ENCLOSE_VALUE_RGX
            )
            .matcher(resultToParse);

        var requestEndProgress = new Request();
        requestEndProgress.setMessage(Interaction.END_INDETERMINATE_PROGRESS);
        requestEndProgress.setParameters(table);
        this.injectionModel.sendToViews(requestEndProgress);

        if (!regexSearch.find()) {
            
            throw new InjectionFailureException("No match while injecting columns");
        }

        regexSearch.reset();

        // Build an array of Column objects from the data we have parsed
        while (regexSearch.find()) {
            
            String nameColumn = regexSearch.group(1);

            var column = new Column(nameColumn, table);
            columns.add(column);
        }

        var requestAddColumns = new Request();
        requestAddColumns.setMessage(Interaction.ADD_COLUMNS);
        requestAddColumns.setParameters(columns);
        this.injectionModel.sendToViews(requestAddColumns);
        
        return columns;
    }

    /**
     * Get table values and count each occurrences and send them to the view.<br>
     * Values are on clear text (not hexa) and follows this window pattern<br>
     * => hh[value 1]jj[count]hhgghh[value 2]jj[count]hhggh...hi<br>
     * Data window can be cut before the end of the request but the process helps to obtain
     * the rest of the unreachable data. The process can be interrupted by the user (stop/pause).
     * @param columns choice by the user
     * @return a 2x2 table containing values by columns
     * @throws JSqlException when injection failure or stopped by user
     */
    public String[][] listValues(List<Column> columns) throws JSqlException {
        
        var database = (Database) columns.get(0).getParent().getParent();
        var table = (Table) columns.get(0).getParent();
        int rowCount = columns.get(0).getParent().getChildCount();

        // Inform the view that table has just been used
        var request = new Request();
        request.setMessage(Interaction.START_PROGRESS);
        request.setParameters(table);
        this.injectionModel.sendToViews(request);

        // Build an array of column names
        List<String> columnsName = new ArrayList<>();
        for (AbstractElementDatabase e: columns) {
            columnsName.add(e.toString());
        }

        // From that array, build the SQL fields nicely
        // => col1{%}col2...
        // ==> trim(ifnull(`col1`,0x00)),0x7f,trim(ifnull(`Col2`,0x00))...
        String[] arrayColumns = columnsName.toArray(new String[columnsName.size()]);

        List<List<String>> listValues = this.getRows(database, table, rowCount, arrayColumns);

        // Add the default title to the columns: row number, occurrence
        columnsName.add(0, StringUtils.EMPTY);
        columnsName.add(0, StringUtils.EMPTY);

        String[][] tableDatas = this.build2D(columnsName, listValues);

        arrayColumns = columnsName.toArray(new String[columnsName.size()]);
        
        // Group the columns names, values and Table object in one array
        var objectData = new Object[]{ arrayColumns, tableDatas, table };

        var requestCreateValuesTab = new Request();
        requestCreateValuesTab.setMessage(Interaction.CREATE_VALUES_TAB);
        requestCreateValuesTab.setParameters(objectData);
        this.injectionModel.sendToViews(requestCreateValuesTab);

        var requestEndProgress = new Request();
        requestEndProgress.setMessage(Interaction.END_PROGRESS);
        requestEndProgress.setParameters(table);
        this.injectionModel.sendToViews(requestEndProgress);
        
        return tableDatas;
    }

    private List<List<String>> getRows(Database database, Table table, int rowCount, String[] columns) throws InjectionFailureException {
        
        String resultToParse = StringUtils.EMPTY;
        
        try {
            var pageSource = new String[]{ StringUtils.EMPTY };
            
            resultToParse = new SuspendableGetRows(this.injectionModel).run(
                this.injectionModel.getMediatorVendor().getVendor().instance().sqlRows(columns, database, table),
                pageSource,
                true,
                rowCount,
                table,
                "rows"
            );
            
        } catch (AbstractSlidingException e) {
            
            LOGGER.log(LogLevel.CONSOLE_ERROR, e.getMessage());
            
            // Get pieces of data already retrieved instead of losing them
            if (StringUtils.isNotEmpty(e.getSlidingWindowAllRows())) {
                
                resultToParse = e.getSlidingWindowAllRows();
                
            } else if (StringUtils.isNotEmpty(e.getSlidingWindowCurrentRows())) {
                
                resultToParse = e.getSlidingWindowCurrentRows();
            }
            
        } catch (Exception e) {
            
            LOGGER.log(LogLevel.CONSOLE_ERROR, e.getMessage(), e);
        }

        return SuspendableGetRows.parse(resultToParse);
    }

    private String[][] build2D(List<String> columnsName, List<List<String>> listValues) {
        
        // Build a proper 2D array from the data
        var tableDatas = new String[listValues.size()][columnsName.size()];
        
        for (var indexRow = 0 ; indexRow < listValues.size() ; indexRow++) {
            
            var isIncomplete = false;
            
            for (var indexColumn = 0 ; indexColumn < columnsName.size() ; indexColumn++) {
                
                try {
                    tableDatas[indexRow][indexColumn] = listValues.get(indexRow).get(indexColumn);
                    
                } catch (IndexOutOfBoundsException e) {
                    
                    isIncomplete = true;
                    
                    LOGGER.log(LogLevel.CONSOLE_DEFAULT, I18nUtil.valueByKey("LOG_LIST_VALUES_INCOMPLETE"));
                    
                    LOGGER.log(LogLevel.IGNORE, e);
                }
            }
            
            if (isIncomplete) {
                
                int logIndexRow = indexRow;
                LOGGER.log(
                    LogLevel.CONSOLE_ERROR,
                    "{}{}: ",
                    () -> I18nUtil.valueByKey("LOG_LIST_VALUES_TOO_LONG"),
                    () -> logIndexRow + 1
                );
                LOGGER.log(
                    LogLevel.CONSOLE_ERROR,
                    () -> String.join(
                        ", ",
                        listValues.get(logIndexRow).toArray(new String[listValues.get(logIndexRow).size()])
                    )
                );
            }
        }
        
        return tableDatas;
    }
}
