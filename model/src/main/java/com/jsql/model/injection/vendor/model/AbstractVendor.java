package com.jsql.model.injection.vendor.model;

import com.jsql.model.bean.database.Database;
import com.jsql.model.bean.database.Table;
import com.jsql.model.injection.strategy.blind.AbstractInjectionBoolean.BooleanMode;
import com.jsql.model.injection.vendor.model.yaml.ModelYaml;

import java.util.List;

public interface AbstractVendor {
    
    ModelYaml getModelYaml();
    
    String sqlTestError();
    
    String sqlInfos();
    String sqlDatabases();
    String sqlTables(Database database);
    String sqlColumns(Table table);
    String sqlRows(String[] arrayColumns, Database database, Table table);

    String sqlNormal(String sqlQuery, String startPosition);
    String sqlError(String sqlQuery, String startPosition);
    String sqlBlind(String sqlQuery, String startPosition);
    String sqlTime(String sqlQuery, String startPosition);
    String sqlStacked(String sqlQuery, String startPosition);

    String sqlCapacity(String[] indexes);
    String sqlIndices(Integer nbFields);
    String sqlOrderBy();
    String sqlLimit(Integer limitSQLResult);
    String endingComment();
    String fingerprintErrorsAsRegex();
    
    String sqlPrivilegeTest();
    String sqlFileRead(String filePath);
    String sqlTextIntoFile(String content, String filePath);

    List<String> getListFalseTest();
    List<String> getListTrueTest();

    String sqlTestBooleanInitialization();
    String sqlTestBlind(String check, BooleanMode blindMode);
    String sqlBitTestBlind(String inj, int indexCharacter, int bit, BooleanMode blindMode);
    String sqlTimeTest(String check, BooleanMode blindMode);
    String sqlBitTestTime(String inj, int indexCharacter, int bit, BooleanMode blindMode);
    String sqlMultibit(String inj, int indexCharacter, int block);

    String sqlBooleanBlind();

    String sqlBooleanTime();
}
