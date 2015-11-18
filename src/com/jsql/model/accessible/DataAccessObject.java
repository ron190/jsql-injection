/*******************************************************************************
 * Copyhacked (H) 2012-2014.
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

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.model.bean.AbstractElementDatabase;
import com.jsql.model.bean.Column;
import com.jsql.model.bean.Database;
import com.jsql.model.bean.Request;
import com.jsql.model.bean.Table;
import com.jsql.model.injection.MediatorModel;
import com.jsql.model.injection.suspendable.SuspendableGetRows;
import com.jsql.tool.ToolsString;

/**
 * Database ressource object to read database, table, columns.
 */
public class DataAccessObject {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(DataAccessObject.class);

    /**
     * Get the initial database informations.<br>
     * => version{%}database{%}user{%}CURRENT_USER
     */
    public void getDatabaseInfos() throws PreparationException, StoppableException {
        LOGGER.trace("Fetching informations...");
        
        String[] sourcePage = {""};

        String hexResult = new SuspendableGetRows().action(
            MediatorModel.model().sqlStrategy.getSchemaInfos(),
            sourcePage,
            false,
            0,
            null
        );

        if ("".equals(hexResult)) {
            MediatorModel.model().sendResponseFromSite("Show db info failed", sourcePage[0].trim());
            /**
             * TODO Extraction Exception
             */
            throw new PreparationException();
        }

        String dbType = "";
        if(MediatorModel.model().sqlStrategy.getDbLabel() != null) {
            dbType = MediatorModel.model().sqlStrategy.getDbLabel() + " ";
        }
        
        MediatorModel.model().versionDatabase   = dbType + hexResult.split("\\{%\\}")[0].replaceAll("\\s+"," ");
        MediatorModel.model().currentDatabase   = hexResult.split("\\{%\\}")[1];
        MediatorModel.model().currentUser       = hexResult.split("\\{%\\}")[2];
        MediatorModel.model().authenticatedUser = hexResult.split("\\{%\\}")[3];

        // Inform the view that info should be displayed
        Request request = new Request();
        request.setMessage("MessageInfo");
        MediatorModel.model().interact(request);
    }
    
    /**
     * Get all databases names and table counts, then address them to the view.<br>
     * We use a hexadecimal format and parse the pattern:<br>
     * => hh[database name 1]jj[number of tables]hhgghh[database name 2]jj[number of tables]hhggh...hi<br>
     * We can't expect that all the data will be found in one request,
     * Stoppable_loopIntoResults helps to obtain the rest of the normally unreachable data.<br>
     * The process can be stopped by the user.
     */
    public List<Database> listDatabases() throws PreparationException, StoppableException {
        LOGGER.trace("Fetching databases...");
        
        String[] sourcePage = {""};
        String hexResult = new SuspendableGetRows().action(
            MediatorModel.model().sqlStrategy.getSchemaList(),
            sourcePage,
            true,
            0,
            null
        );

        // Parse all data we have retrieved
        Matcher regexSearch = Pattern.compile(
                "\\x04([^\\x01-\\x09\\x0B-\\x0C\\x0E-\\x1F]*)\\x05([^\\x01-\\x09\\x0B-\\x0C\\x0E-\\x1F]*)(\\x08)?\\x04",
                Pattern.CASE_INSENSITIVE | Pattern.DOTALL
        ).matcher(hexResult);

        if (!regexSearch.find()) {
            /**
             * TODO Extraction Exception
             */
            MediatorModel.model().sendResponseFromSite("Fetching databases fails", sourcePage[0].trim());
            throw new PreparationException();
        }
        regexSearch.reset();

        // Build an array of Database objects from the data we have parsed
        List<Database> databases = new ArrayList<Database>();
        while (regexSearch.find()) {
            String databaseName = regexSearch.group(1);
            String tableCount = regexSearch.group(2);

            Database newDatabase = new Database(databaseName, tableCount.toString());
            databases.add(newDatabase);
        }

        // Address these objects to the view
        Request request = new Request();
        request.setMessage("AddDatabases");
        request.setParameters(databases);
        MediatorModel.model().interact(request);
        
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
    public List<Table> listTables(Database database)
            throws NumberFormatException, PreparationException, StoppableException {
        List<Table> tables = new ArrayList<Table>();
        
        // Inform the view that database has just been used
        Request request = new Request();
        request.setMessage("StartProgress");
        request.setParameters(database);
        MediatorModel.model().interact(request);

        String tableCount = Integer.toString(database.getCount());

        String[] pageSource = {""};
        String hexResult = new SuspendableGetRows().action(
            MediatorModel.model().sqlStrategy.getTableList(database),
            pageSource,
            true,
            Integer.parseInt(tableCount),
            database
        );

        // Parse all the data we have retrieved
        Matcher regexSearch =
                Pattern.compile(
                        "\\x04([^\\x01-\\x09\\x0B-\\x0C\\x0E-\\x1F]*)\\x05([^\\x01-\\x09\\x0B-\\x0C\\x0E-\\x1F]*)(\\x08)?\\x04",
                        Pattern.CASE_INSENSITIVE | Pattern.DOTALL
                ).matcher(hexResult);

        if (!regexSearch.find()) {
            MediatorModel.model().sendResponseFromSite("Fetching tables fails", pageSource[0].trim());
        } else {
            regexSearch.reset();

            // Build an array of Table objects from the data we have parsed
            while (regexSearch.find()) {
                String tableName = regexSearch.group(1);
                String rowCount  = regexSearch.group(2);

                Table newTable = new Table(tableName, rowCount, database);
                tables.add(newTable);
            }

            // Address these objects to the view
            Request request2 = new Request();
            request2.setMessage("AddTables");
            request2.setParameters(tables);
            MediatorModel.model().interact(request2);
        }

        // Inform the view that database job is finished
        Request request3 = new Request();
        request3.setMessage("EndProgress");
        request3.setParameters(database);
        MediatorModel.model().interact(request3);
        
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
    public List<Column> listColumns(Table table) throws PreparationException, StoppableException {
        List<Column> columns = new ArrayList<Column>();
        
        // Inform the view that table has just been used
        Request request = new Request();
        request.setMessage("StartIndeterminateProgress");
        request.setParameters(table);
        MediatorModel.model().interact(request);

        String[] pageSource = {""};
        String hexResult = new SuspendableGetRows().action(
            MediatorModel.model().sqlStrategy.getColumnList(table),
            pageSource,
            true,
            0,
            table
        );

        // Parse all the data we have retrieved
        Matcher regexSearch = Pattern.compile(
                "\\x04([^\\x01-\\x09\\x0B-\\x0C\\x0E-\\x1F]*)\\x05([^\\x01-\\x09\\x0B-\\x0C\\x0E-\\x1F]*)(\\x08)?\\x04",
                Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(hexResult);

        if (!regexSearch.find()) {
            MediatorModel.model().sendResponseFromSite("Fetching columns fails", pageSource[0].trim());
        } else {
            regexSearch.reset();

            // Build an array of Column objects from the data we have parsed
            while (regexSearch.find()) {
                String columnName = regexSearch.group(1);

                Column newColumn = new Column(columnName, table);
                columns.add(newColumn);
            }

            // Address these objects to the view
            Request request2 = new Request();
            request2.setMessage("AddColumns");
            request2.setParameters(columns);
            MediatorModel.model().interact(request2);
        }

        // Inform the view that table job is finished
        Request request3 = new Request();
        request3.setMessage("EndIndeterminateProgress");
        request3.setParameters(table);
        MediatorModel.model().interact(request3);
        
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
    public String[][] listValues(List<Column> argsElementDatabase) throws PreparationException, StoppableException {
        Database database = (Database) argsElementDatabase.get(0).getParent().getParent();
        Table table = (Table) argsElementDatabase.get(0).getParent();
        int rowCount = argsElementDatabase.get(0).getParent().getCount();

        // Inform the view that table has just been used
        Request request = new Request();
        request.setMessage("StartProgress");
        request.setParameters(table);
        MediatorModel.model().interact(request);

        // Build an array of column names
        List<String> columnsName = new ArrayList<String>();
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
        String hexResult = new SuspendableGetRows().action(
            MediatorModel.model().sqlStrategy.getValues(arrayColumns, database, table),
            pageSource, 
            true, 
            rowCount, 
            table
        );

        // Parse all the data we have retrieved
        Matcher regexSearch = Pattern.compile(
                "\\x04([^\\x01-\\x09\\x0B-\\x0C\\x0E-\\x1F]*?)\\x05([^\\x01-\\x09\\x0B-\\x0C\\x0E-\\x1F]*?)(\\x08)?\\x04",
                Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(hexResult);

        if (!regexSearch.find()) {
            MediatorModel.model().sendResponseFromSite("Fetching values fails (row count can be inaccurate)", pageSource[0].trim());
        }
        regexSearch.reset();

        int rowsFound = 0/*, duplicates = 0, cutted = 0*/;
        List<List<String>> listValues = new ArrayList<List<String>>();

        // Build a 2D array of strings from the data we have parsed
        // => row number, occurrence, value1, value2...
        while (regexSearch.find()) {
            String values = regexSearch.group(1);
            int instances = Integer.parseInt(regexSearch.group(2));
            /*if(regexSearch.group(3) != null)
                cutted++;*/

            listValues.add(new ArrayList<String>());
            listValues.get(rowsFound).add("" + (rowsFound + 1) + " x" + instances);
//            listValues.get(rowsFound).add("" + instances);
            for (String cellValue: values.split("\\x7F", -1)) {
                listValues.get(rowsFound).add(cellValue);
            }
            /*duplicates += instances - 1;*/
            rowsFound++;
            //            System.out.println( rowsFound + ". "+ instances +"x "+  values.replace("00", "").replace("\r\n", "").replace("\n", "").replace("\r", "") );
        }

        //        System.out.println( "# Results: "+ duplicates +" duplicates, "+ rowsFound +" distinct values, " /*+ (rowCount-rowsFound-duplicates) +" unreachables duplicates, "*/ + cutted + " rows truncated\n");

        // Add the default title to the columns: row number, occurrence
//        columnsName.add(0, "duplicate");
        columnsName.add(0, "");

        // Build a proper 2D array from the data
        String[][] tableDatas = new String[listValues.size()][columnsName.size()];
        for (int indexRow = 0; indexRow < listValues.size(); indexRow++) {
            boolean isIncomplete = false;
            for (int indexColumn = 0; indexColumn < columnsName.size(); indexColumn++) {
                try {
                    tableDatas[indexRow][indexColumn] = listValues.get(indexRow).get(indexColumn);
                } catch (IndexOutOfBoundsException e) {
                    isIncomplete = true;
                }
            }
            if (isIncomplete) {
                LOGGER.warn("String is too long, row #" + (indexRow + 1) + " is incomplete:");
                LOGGER.warn(ToolsString.join(listValues.get(indexRow).toArray(new String[listValues.get(indexRow).size()]), ", "));
            }
        }

        arrayColumns = columnsName.toArray(new String[columnsName.size()]);
        // Group the columns names, values and Table object in one array
        Object[] objectData = {arrayColumns, tableDatas, table};

        // Address these objects to the view
        Request request2 = new Request();
        request2.setMessage("CreateValuesTab");
        request2.setParameters(objectData);
        MediatorModel.model().interact(request2);

        // Inform the view that table job is finished
        Request request3 = new Request();
        request3.setMessage("EndProgress");
        request3.setParameters(table);
        MediatorModel.model().interact(request3);
        
        return tableDatas;
    }
}
