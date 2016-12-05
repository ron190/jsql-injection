/*******************************************************************************
 * Copyhacked (H) 2012-2016.
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.jsql.model.MediatorModel;
import com.jsql.model.bean.database.AbstractElementDatabase;
import com.jsql.model.bean.database.Column;
import com.jsql.model.bean.database.Database;
import com.jsql.model.bean.database.Table;
import com.jsql.model.bean.util.Request;
import com.jsql.model.bean.util.TypeRequest;
import com.jsql.model.exception.InjectionFailureException;
import com.jsql.model.exception.JSqlException;
import com.jsql.model.exception.SlidingException;
import com.jsql.model.exception.StoppedByUserException;
import com.jsql.model.injection.vendor.Vendor;
import com.jsql.model.suspendable.SuspendableGetRows;
import com.jsql.util.StringUtil;

/**
 * Database ressource object to read database, table, columns.
 */
public class DataAccess {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
    public static final String TRAIL_SQL = "%01%03%03%07";
    
    public static final String TD = "\\x06";
    public static final String TD_SQL = "%06";
    
    public static final String QTE_SQL = "%05";
    
    public static final String SEPARATOR = "\\x04";
    public static final String SEPARATOR_SQL = "%04";
    
    public static final String MODE = "(?si)";
    public static final String LINE = "([^\\x01-\\x09\\x0B-\\x0C\\x0E-\\x1F]*)\\x05([^\\x01-\\x09\\x0B-\\x0C\\x0E-\\x1F]*)(\\x08)?";
    
    private DataAccess() {
        // Utility
    }
    
    /**
     * Get the initial database informations.<br>
     * => version{%}database{%}user{%}CURRENT_USER
     * @throws StoppedByUserException 
     */
    public static void getDatabaseInfos() throws JSqlException {
        LOGGER.trace("Fetching informations...");
        
        String[] sourcePage = {""};

        String resultToParse;
        resultToParse = new SuspendableGetRows().run(
            MediatorModel.model().vendor.instance().sqlInfos(),
            sourcePage,
            false,
            0,
            null
        );

        if ("".equals(resultToParse)) {
            MediatorModel.model().sendResponseFromSite("Incorrect database informations", sourcePage[0].trim());
        }

        // Check if parsing is failing
        try {
            MediatorModel.model().setDatabaseInfos(
                resultToParse.split(SEPARATOR)[0].replaceAll("\\s+"," "),
                resultToParse.split(SEPARATOR)[1],
                resultToParse.split(SEPARATOR)[2]
            );
        } catch (ArrayIndexOutOfBoundsException e) {
            LOGGER.warn("Incorrect database informations: "+ resultToParse, e);
            throw new InjectionFailureException("Unrecognized information on database");
        }
        
        LOGGER.debug(MediatorModel.model().getDatabaseInfos());
    }
    
    /**
     * Get all databases names and table counts, then address them to the view.<br>
     * We use a hexadecimal format and parse the pattern:<br>
     * => hh[database name 1]jj[number of tables]hhgghh[database name 2]jj[number of tables]hhggh...hi<br>
     * We can't expect that all the data will be found in one request,
     * Stoppable_loopIntoResults helps to obtain the rest of the normally unreachable data.<br>
     * The process can be stopped by the user.
     * @throws StoppedByUserException 
     */
    public static List<Database> listDatabases() throws JSqlException {
        LOGGER.trace("Fetching databases...");
        
        List<Database> databases = new ArrayList<>();
        
        String resultToParse = "";
        try {
            String[] sourcePage = {""};
            resultToParse = new SuspendableGetRows().run(
                MediatorModel.model().vendor.instance().sqlDatabases(),
                sourcePage,
                true,
                0,
                null
            );
        } catch (SlidingException e) {
            LOGGER.warn(e.getMessage(), e);
            // Get pieces of data already retreive instead of losing them
            if (!e.getSlidingWindowAllRows().equals("")) {
                resultToParse = e.getSlidingWindowAllRows();
            } else if (!e.getSlidingWindowCurrentRows().equals("")) {
                resultToParse = e.getSlidingWindowCurrentRows();
            }
        }

        // Parse all data we have retrieved
        Matcher regexSearch = 
            Pattern
                .compile(MODE + SEPARATOR + LINE + SEPARATOR)
                .matcher(resultToParse);

        if (!regexSearch.find()) {
            throw new InjectionFailureException("Unrecognized name of databases");
        }
        regexSearch.reset();

        // Build an array of Database objects from the data we have parsed
        while (regexSearch.find()) {
            String databaseName = regexSearch.group(1);
            String tableCount = regexSearch.group(2);

            Database newDatabase = new Database(databaseName, tableCount);
            databases.add(newDatabase);
        }

        Request request = new Request();
        request.setMessage(TypeRequest.ADD_DATABASES);
        request.setParameters(databases);
        MediatorModel.model().sendToViews(request);
        
        return databases;
    }

    /**
     * Get all tables names and row counts, then address them to the view.<br>
     * We use a hexadecimal format and parse the pattern:<br>
     * => hh[table name 1]jj[number of rows]hhgghh[table name 2]jj[number of rows]hhggh...hi<br>
     * We can't expect that all the data will be found in one request,
     * Stoppable_loopIntoResults helps to obtain the rest of the unreachable data.<br>
     * The process can be interrupted by the user (stop/pause).
     */
    public static List<Table> listTables(Database database) throws JSqlException {
        List<Table> tables = new ArrayList<>();
        
        // Inform the view that database has just been used
        Request request = new Request();
        request.setMessage(TypeRequest.START_PROGRESS);
        request.setParameters(database);
        MediatorModel.model().sendToViews(request);

        String tableCount = Integer.toString(database.getCount());
        
        String resultToParse = "";
        try {
            String[] pageSource = {""};
            resultToParse = new SuspendableGetRows().run(
                MediatorModel.model().vendor.instance().sqlTables(database),
                pageSource,
                true,
                Integer.parseInt(tableCount),
                database
            );
        } catch (SlidingException e) {
            LOGGER.warn(e.getMessage(), e);
            // Get pieces of data already retreive instead of losing them
            if (!e.getSlidingWindowAllRows().equals("")) {
                resultToParse = e.getSlidingWindowAllRows();
            } else if (!e.getSlidingWindowCurrentRows().equals("")) {
                resultToParse = e.getSlidingWindowCurrentRows();
            }
        }

        // Parse all the data we have retrieved
        Matcher regexSearch =
            Pattern
                .compile(MODE + SEPARATOR + LINE + SEPARATOR)
                .matcher(resultToParse);
        
        if (!regexSearch.find()) {
            throw new InjectionFailureException("Unrecognized name of tables");
        } else {
            regexSearch.reset();
            
            // Build an array of Table objects from the data we have parsed
            while (regexSearch.find()) {
                String tableName = regexSearch.group(1);
                String rowCount  = regexSearch.group(2);
                
                Table newTable = new Table(tableName, rowCount, database);
                tables.add(newTable);
            }
        }
        
        return tables;
    }

    /**
     * Get all columns names (we force count to 1, then ignore it),
     * then address them to the view.<br>
     * We use a hexadecimal format and parse the pattern:<br>
     * => hh[column name 1]jj31hhgghh[column name 2]jj31hhggh...hi<br>
     * We can't expect that all the data will be found in one request,
     * Stoppable_loopIntoResults helps to obtain the rest of the unreachable data.<br>
     * The process can be interrupted by the user (stop/pause)
     */
    public static List<Column> listColumns(Table table) throws JSqlException {
        List<Column> columns = new ArrayList<>();
        
        // Inform the view that table has just been used
        Request request = new Request();
        request.setMessage(TypeRequest.START_INDETERMINATE_PROGRESS);
        request.setParameters(table);
        MediatorModel.model().sendToViews(request);

        String resultToParse = "";
        try {
            String[] pageSource = {""};
            resultToParse = new SuspendableGetRows().run(
                MediatorModel.model().vendor.instance().sqlColumns(table),
                pageSource,
                true,
                0,
                table
            );
        } catch (SlidingException e) {
            LOGGER.warn(e.getMessage(), e);
            // Get pieces of data already retreive instead of losing them
            if (!e.getSlidingWindowAllRows().equals("")) {
                resultToParse = e.getSlidingWindowAllRows();
            } else if (!e.getSlidingWindowCurrentRows().equals("")) {
                resultToParse = e.getSlidingWindowCurrentRows();
            }
        }

        // Build SQLite columns
        if (MediatorModel.model().vendor == Vendor.SQLITE) {
            String resultSQLite = "";
            String resultTmp = resultToParse.replaceFirst(".+?\\(", "").trim().replaceAll("\\)$", "");
            resultTmp = resultTmp.replaceAll("\\(.+?\\)", "");
            for (String columnNameAndType: resultTmp.split(",")) {
                String columnName = columnNameAndType.trim().split(" ")[0];
                if (!"CONSTRAINT".equals(columnName) && !"UNIQUE".equals(columnName)) {
                    resultSQLite += (char) 4 + columnName + (char) 5 + "0" + (char) 4 + (char) 6;
                }
            }
            resultToParse = resultSQLite;
        }
        
        // Parse all the data we have retrieved
        Matcher regexSearch = 
            Pattern
                .compile(MODE + SEPARATOR + LINE + SEPARATOR)
                .matcher(resultToParse);

        if (!regexSearch.find()) {
            throw new InjectionFailureException("Unrecognized name of columns");
        } else {
            regexSearch.reset();

            // Build an array of Column objects from the data we have parsed
            while (regexSearch.find()) {
                String columnName = regexSearch.group(1);

                Column newColumn = new Column(columnName, table);
                columns.add(newColumn);
            }

            Request requestAddColumns = new Request();
            requestAddColumns.setMessage(TypeRequest.ADD_COLUMNS);
            requestAddColumns.setParameters(columns);
            MediatorModel.model().sendToViews(requestAddColumns);
        }

        Request request3 = new Request();
        request3.setMessage(TypeRequest.END_INDETERMINATE_PROGRESS);
        request3.setParameters(table);
        MediatorModel.model().sendToViews(request3);
        
        return columns;
    }

    /**
     * Get all values and their occurrences (we use GROUP BY),
     * then address them to the view.<br>
     * We use a hexadecimal format and parse the pattern<br>
     * => hh[value 1]jj[occurence]hhgghh[value 2]jj[occurence]hhggh...hi<br>
     * We can't expect that all the data will be found in one request,
     * Stoppable_loopIntoResults helps to obtain the rest of the
     * unreachable data.<br>
     * The process can be interrupted by the user (stop/pause).
     * @param values columns selected by user
     * @return
     */
    public static String[][] listValues(List<Column> argsElementDatabase) throws JSqlException {
        Database database = (Database) argsElementDatabase.get(0).getParent().getParent();
        Table table = (Table) argsElementDatabase.get(0).getParent();
        int rowCount = argsElementDatabase.get(0).getParent().getCount();

        // Inform the view that table has just been used
        Request request = new Request();
        request.setMessage(TypeRequest.START_PROGRESS);
        request.setParameters(table);
        MediatorModel.model().sendToViews(request);

        // Build an array of column names
        List<String> columnsName = new ArrayList<>();
        for (AbstractElementDatabase e: argsElementDatabase) {
            columnsName.add(e.toString());
        }

        /*
         * From that array, build the SQL fields nicely
         * =>  col1{%}col2...
         * ==> trim(ifnull(`col1`,0x00)),0x7f,trim(ifnull(`Col2`,0x00))...
         */
        String[] arrayColumns = columnsName.toArray(new String[columnsName.size()]);

        String[] pageSource = {""};
        String resultToParse = new SuspendableGetRows().run(
            MediatorModel.model().vendor.instance().sqlRows(arrayColumns, database, table),
            pageSource, 
            true, 
            rowCount, 
            table
        );

        // Parse all the data we have retrieved
        Matcher regexSearch = 
            Pattern
                .compile(MODE + SEPARATOR +"([^\\x01-\\x09\\x0B-\\x0C\\x0E-\\x1F]*?)\\x05([^\\x01-\\x09\\x0B-\\x0C\\x0E-\\x1F]*?)(\\x08)?"+ SEPARATOR)
                .matcher(resultToParse);

        if (!regexSearch.find()) {
            MediatorModel.model().sendResponseFromSite("Fetching values fails (row count can be inaccurate)", pageSource[0].trim());
        }
        regexSearch.reset();

        int rowsFound = 0;
        List<List<String>> listValues = new ArrayList<>();

        // Build a 2D array of strings from the data we have parsed
        // => row number, occurrence, value1, value2...
        while (regexSearch.find()) {
            String values = regexSearch.group(1);
            int instances = Integer.parseInt(regexSearch.group(2));

            listValues.add(new ArrayList<String>());
            listValues.get(rowsFound).add(rowsFound + 1 +"");
            listValues.get(rowsFound).add("x"+ instances);
            for (String cellValue: values.split("\\x7F", -1)) {
                listValues.get(rowsFound).add(cellValue);
            }

            rowsFound++;
        }

        // Add the default title to the columns: row number, occurrence
        columnsName.add(0, "");
        columnsName.add(0, "");

        // Build a proper 2D array from the data
        String[][] tableDatas = new String[listValues.size()][columnsName.size()];
        for (int indexRow = 0 ; indexRow < listValues.size() ; indexRow++) {
            boolean isIncomplete = false;
            for (int indexColumn = 0 ; indexColumn < columnsName.size() ; indexColumn++) {
                try {
                    tableDatas[indexRow][indexColumn] = listValues.get(indexRow).get(indexColumn);
                } catch (IndexOutOfBoundsException e) {
                    isIncomplete = true;
                    LOGGER.trace("Incomplete line found", e);
                }
            }
            if (isIncomplete) {
                LOGGER.warn("String is too long, row #"+ (indexRow + 1) +" is incomplete:");
                LOGGER.warn(StringUtil.join(listValues.get(indexRow).toArray(new String[listValues.get(indexRow).size()]), ", "));
            }
        }

        arrayColumns = columnsName.toArray(new String[columnsName.size()]);
        
        // Group the columns names, values and Table object in one array
        Object[] objectData = {arrayColumns, tableDatas, table};

        Request requestCreateValuesTab = new Request();
        requestCreateValuesTab.setMessage(TypeRequest.CREATE_VALUES_TAB);
        requestCreateValuesTab.setParameters(objectData);
        MediatorModel.model().sendToViews(requestCreateValuesTab);

        Request requestEndProgress = new Request();
        requestEndProgress.setMessage(TypeRequest.END_PROGRESS);
        requestEndProgress.setParameters(table);
        MediatorModel.model().sendToViews(requestEndProgress);
        
        return tableDatas;
    }
}
