package com.jsql.model.injection.vendor.model;

import com.jsql.model.bean.database.Database;
import com.jsql.model.bean.database.Table;
import com.jsql.model.injection.strategy.blind.AbstractInjectionBinary.BinaryMode;
import com.jsql.model.injection.vendor.model.yaml.Method;
import com.jsql.model.injection.vendor.model.yaml.ModelYaml;

import java.util.List;

public interface AbstractVendor {
    
    ModelYaml getModelYaml();
    
    String sqlInfos();
    String sqlDatabases();
    String sqlTables(Database database);
    String sqlColumns(Table table);
    String sqlRows(String[] arrayColumns, Database database, Table table);

    String sqlUnion(String sqlQuery, String startPosition, boolean isReport);
    String sqlErrorIndice(Method errorMethod);
    String sqlErrorCalibrator(Method errorMethod);
    String sqlError(String sqlQuery, String startPosition, int indexMethodError, boolean isReport);
    String sqlBlind(String sqlQuery, String startPosition, boolean isReport);
    String sqlTime(String sqlQuery, String startPosition, boolean isReport);
    String sqlStack(String sqlQuery, String startPosition, boolean isReport);

    String sqlCapacity(String[] indexes);
    String sqlIndices(Integer nbFields);
    String sqlOrderBy();
    String sqlLimit(Integer limitSqlResult);
    String endingComment();
    String fingerprintErrorsAsRegex();
    
    String sqlPrivilegeTest();
    String sqlFileRead(String path);
    String sqlTextIntoFile(String body, String path);

    List<String> getFalsy();
    List<String> getTruthy();

    String sqlTestBinaryInitialization();
    String sqlTestBlind(String check, BinaryMode blindMode);
    String sqlBitTestBlind(String inj, int indexCharacter, int bit, BinaryMode blindMode);
    String sqlTimeTest(String check, BinaryMode blindMode);
    String sqlBitTestTime(String inj, int indexCharacter, int bit, BinaryMode blindMode);
    String sqlMultibit(String inj, int indexCharacter, int block);

    String sqlBinaryBlind();

    String sqlBinaryTime();
}
