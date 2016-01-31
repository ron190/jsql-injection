package com.jsql.model.vendor;

import com.jsql.model.bean.Database;
import com.jsql.model.bean.Table;

public abstract class AbstractVendorStrategy {    
    
    public abstract String getSchemaInfos();
    public abstract String getSchemaList();
    public abstract String getTableList(Database database);
    public abstract String getColumnList(Table table);
    public abstract String getValues(String[] arrayColumns, Database database, Table table);

    public abstract String normalStrategy(String sqlQuery, String startPosition);
    
    public abstract String getIndicesCapacity(String[] indexes);
    public abstract String getIndices(Integer nbFields);
    public abstract String getOrderBy();
    
    public abstract String getLimit(Integer limitSQLResult);
    
    public String getPrivilege() {
        return "";
    }

    public String readTextFile(String filePath) {
        return "";
    }

    public String writeTextFile(String content, String filePath) {
        return "";
    }

    public String[] getListFalseTest() {
        return new String[0];
    }

    public String[] getListTrueTest() {
        return new String[0];
    }

    public String getBlindFirstTest() {
        return null;
    }

    public String blindCheck(String check) {
        return "";
    }

    public String blindBitTest(String inj, int indexCharacter, int bit) {
        return "";
    }

    public String blindLengthTest(String inj, int indexCharacter) {
        return "";
    }

    public String timeCheck(String check) {
        return "";
    }

    public String timeBitTest(String inj, int indexCharacter, int bit) {
        return "";
    }

    public String timeLengthTest(String inj, int indexCharacter) {
        return "";
    }

    public String blindStrategy(String sqlQuery, String startPosition) {
        return "";
    }

    public String getErrorBasedStrategyCheck() {
        return "";
    }

    public String errorBasedStrategy(String sqlQuery, String startPosition) {
        return "";
    }

    public String timeStrategy(String sqlQuery, String startPosition) {
        return "";
    }
}
