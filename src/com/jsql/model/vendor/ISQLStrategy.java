package com.jsql.model.vendor;

import com.jsql.model.bean.Database;
import com.jsql.model.bean.Table;

public interface ISQLStrategy {
    String getDbLabel();
    
    String getSchemaInfos();
    String getSchemaList();
    String getTableList(Database database);
    String getColumnList(Table table);
    String getValues(String[] arrayColumns, Database database, Table table);
    
    String getPrivilege();
    String readTextFile(String filePath);
    String writeTextFile(String content, String filePath);
    
    String[] getListFalseTest();
    String[] getListTrueTest();
    String getBlindFirstTest();
    String blindCheck(String check);
    String blindBitTest(String inj, int indexCharacter, int bit);
    String blindLengthTest(String inj, int indexCharacter);
    String timeCheck(String check);
    String timeBitTest(String inj, int indexCharacter, int bit);
    String timeLengthTest(String inj, int indexCharacter);
    
    String blindStrategy(String sqlQuery, String startPosition); 
    String getErrorBasedStrategyCheck();
    String errorBasedStrategy(String sqlQuery, String startPosition); 
    String normalStrategy(String sqlQuery, String startPosition); 
    String timeStrategy(String sqlQuery, String startPosition); 
    
    String performanceQuery(String[] indexes);
    String initialQuery(Integer nbFields);
    String insertionCharacterQuery();
    
    String getLimit(Integer limitSQLResult);
}
