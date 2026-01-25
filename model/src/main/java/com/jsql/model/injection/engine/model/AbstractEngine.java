package com.jsql.model.injection.engine.model;

import com.jsql.model.bean.database.Database;
import com.jsql.model.bean.database.Table;
import com.jsql.model.injection.strategy.blind.AbstractInjectionBit.BlindOperator;
import com.jsql.model.injection.engine.model.yaml.Method;
import com.jsql.model.injection.engine.model.yaml.ModelYaml;

import java.util.List;

public interface AbstractEngine {
    
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
    String sqlDns(String sqlQuery, String startPosition, BlindOperator blindOperator, boolean isReport);

    String sqlCapacity(String[] indexes);
    String sqlIndices(Integer nbFields);
    String sqlOrderBy();
    String sqlLimit(Integer limitSqlResult);
    String endingComment();
    String fingerprintErrorsAsRegex();

    List<String> getFalsyBit();
    List<String> getTruthyBit();
    List<String> getFalsyBin();
    List<String> getTruthyBin();

    String sqlBlindConfirm();
    String sqlTestBlindWithOperator(String check, BlindOperator blindOperator);
    String sqlBlindBit(String inj, int indexChar, int bit, BlindOperator blindOperator);
    String sqlBlindBin(String inj, int indexChar, int mid, BlindOperator blindOperator);

    String sqlTestTimeWithOperator(String check, BlindOperator blindOperator);
    String sqlTimeBit(String inj, int indexChar, int bit, BlindOperator blindOperator);
    String sqlMultibit(String inj, int indexChar, int block);
}
