package com.jsql.model.injection.vendor;

import com.jsql.model.bean.database.Database;
import com.jsql.model.bean.database.Table;

public abstract class AbstractVendor {    
    
    public abstract String sqlInfos();
    public abstract String sqlDatabases();
    public abstract String sqlTables(Database database);
    public abstract String sqlColumns(Table table);
    public abstract String sqlRows(String[] arrayColumns, Database database, Table table);

    public abstract String sqlNormal(String sqlQuery, String startPosition);
    
    public abstract String sqlCapacity(String[] indexes);
    public abstract String sqlIndices(Integer nbFields);
    public abstract String sqlOrderBy();
    
    public abstract String sqlLimit(Integer limitSQLResult);
    
    public abstract String sqlPrivilegeTest();

    public abstract String sqlFileRead(String filePath);

    public abstract String sqlTextIntoFile(String content, String filePath);

    public abstract String[] getListFalseTest();

    public abstract String[] getListTrueTest();

    public abstract String sqlTestBlindFirst();

    public abstract String sqlTestBlind(String check);

    public abstract String sqlBitTestBlind(String inj, int indexCharacter, int bit);

    public abstract String sqlLengthTestBlind(String inj, int indexCharacter);

    public abstract String sqlTimeTest(String check);

    public abstract String sqlBitTestTime(String inj, int indexCharacter, int bit);

    public abstract String sqlLengthTestTime(String inj, int indexCharacter);

    public abstract String sqlBlind(String sqlQuery, String startPosition);

    public abstract String sqlTestErrorBased();

    public abstract String sqlErrorBased(String sqlQuery, String startPosition);

    public abstract String sqlTime(String sqlQuery, String startPosition);
}
