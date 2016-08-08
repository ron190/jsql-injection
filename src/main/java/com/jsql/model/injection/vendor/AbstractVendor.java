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
    
    public abstract String getSqlCapacity(String[] indexes);
    public abstract String getSqlIndices(Integer nbFields);
    public abstract String getSqlOrderBy();
    
    public abstract String getSqlLimit(Integer limitSQLResult);
    
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
