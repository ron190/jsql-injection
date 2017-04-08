package com.jsql.model.injection.vendor;

import com.jsql.model.bean.database.Database;
import com.jsql.model.bean.database.Table;

interface AbstractVendor {
    
    public String sqlInfos();
    public String sqlDatabases();
    public String sqlTables(Database database);
    public String sqlColumns(Table table);
    public String sqlRows(String[] arrayColumns, Database database, Table table);

    public String sqlNormal(String sqlQuery, String startPosition);
    
    public String sqlCapacity(String[] indexes);
    public String sqlIndices(Integer nbFields);
    public String sqlOrderBy();
    
    public String sqlLimit(Integer limitSQLResult);
    
    public String sqlPrivilegeTest();

    public String sqlFileRead(String filePath);

    public String sqlTextIntoFile(String content, String filePath);

    public String[] getListFalseTest();

    public String[] getListTrueTest();

    public String sqlTestBlindFirst();

    public String sqlTestBlind(String check);

    public String sqlBitTestBlind(String inj, int indexCharacter, int bit);

    public String sqlLengthTestBlind(String inj, int indexCharacter);

    public String sqlTimeTest(String check);

    public String sqlBitTestTime(String inj, int indexCharacter, int bit);

    public String sqlLengthTestTime(String inj, int indexCharacter);

    public String sqlBlind(String sqlQuery, String startPosition);

    public String sqlCapacityErrorBased();
    public String sqlTestErrorBased();
    public String sqlErrorBasedCapacity();
    public Model getXmlModel();

    public String sqlErrorBased(String sqlQuery, String startPosition);

    public String sqlTime(String sqlQuery, String startPosition);
    
}
