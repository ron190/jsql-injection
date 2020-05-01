package com.test.vendor._ingres;

import com.test.AbstractTestSuite;

public abstract class ConcreteIngresTestNopeSuite extends AbstractTestSuite {

//    public ConcreteIngresTestNopeSuite() throws ClassNotFoundException, SQLException {
//
//        this.jdbcURL = "jdbc:ingres://"+ AbstractTestSuite.HOSTNAME +":II7/demodb";
//
//        this.jdbcUser = StringUtils.EMPTY;
//        this.jdbcPass = StringUtils.EMPTY;
//        this.jsqlDatabaseName = "watthieu-x64";
//        this.jsqlTableName = "country";
//        this.jsqlColumnName = "ct_code";
//
//        this.jdbcColumnForDatabaseName = "schema_name";
//        this.jdbcColumnForTableName = "table_name";
//        this.jdbcColumnForColumnName = "column_name";
//
//        this.jdbcQueryForDatabaseNames = "select distinct trim(schema_name) schema_name from iischema";
//        this.jdbcQueryForTableNames = "select distinct trim(table_name) table_name from iiingres_tables where table_owner = 'watthieu-x64'";
//        this.jdbcQueryForColumnNames = "select distinct trim(column_name) column_name from iiocolumns where table_owner = 'watthieu-x64' and table_name = 'country'";
//        this.jdbcQueryForValues = "select distinct ct_code from \"watthieu-x64\".country";
//
//        this.requestJdbc();
//    }
}
