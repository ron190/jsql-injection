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
    
    @Override
    public String toString() {
        return this.getDbLabel();
    }
}
