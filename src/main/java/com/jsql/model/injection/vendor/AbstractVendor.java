package com.jsql.model.injection.vendor;

import com.jsql.model.bean.database.Database;
import com.jsql.model.bean.database.Table;

public abstract class AbstractVendor {    
    
    public abstract String getSqlInfos();
    public abstract String getSqlDatabases();
    public abstract String getSqlTables(Database database);
    public abstract String getSqlColumns(Table table);
    public abstract String getSqlRows(String[] arrayColumns, Database database, Table table);

    public abstract String getSqlNormal(String sqlQuery, String startPosition);
    
    public abstract String getSqlIndicesCapacityCheck(String[] indexes);
    public abstract String getSqlIndices(Integer nbFields);
    public abstract String getSqlOrderBy();
    
    public abstract String getSqlLimit(Integer limitSQLResult);
    
    public String getSqlPrivilegeCheck() {
        return "";
    }

    public String getSqlReadFile(String filePath) {
        return "";
    }

    public String getSqlTextIntoFile(String content, String filePath) {
        return "";
    }

    public String[] getListFalseTest() {
        return new String[0];
    }

    public String[] getListTrueTest() {
        return new String[0];
    }

    public String getSqlBlindFirstTest() {
        return null;
    }

    public String getSqlBlindCheck(String check) {
        return "";
    }

    public String getSqlBlindBitCheck(String inj, int indexCharacter, int bit) {
        return "";
    }

    public String getSqlBlindLengthCheck(String inj, int indexCharacter) {
        return "";
    }

    public String getSqlTimeCheck(String check) {
        return "";
    }

    public String getSqlTimeBitCheck(String inj, int indexCharacter, int bit) {
        return "";
    }

    public String getSqlTimeLengthCheck(String inj, int indexCharacter) {
        return "";
    }

    public String getSqlBlind(String sqlQuery, String startPosition) {
        return "";
    }

    public String getSqlErrorBasedCheck() {
        return "";
    }

    public String getSqlErrorBased(String sqlQuery, String startPosition) {
        return "";
    }

    public String getSqlTime(String sqlQuery, String startPosition) {
        return "";
    }
}
