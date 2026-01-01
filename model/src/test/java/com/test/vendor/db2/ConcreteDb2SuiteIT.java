package com.test.vendor.db2;

import com.test.AbstractTestSuite;
import org.hibernate.cfg.JdbcSettings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import spring.SpringApp;

public abstract class ConcreteDb2SuiteIT extends AbstractTestSuite {

    public ConcreteDb2SuiteIT() {
        this.config();
    }
    
    public void config() {

        this.jdbcURL = SpringApp.propsDb2.getProperty(JdbcSettings.JAKARTA_JDBC_URL);
        this.jdbcUser = SpringApp.propsDb2.getProperty(JdbcSettings.JAKARTA_JDBC_USER);
        this.jdbcPass = SpringApp.propsDb2.getProperty(JdbcSettings.JAKARTA_JDBC_PASSWORD);

        this.jsqlDatabaseName = "DB2INST1";
        this.jsqlTableName = "STUDENT";
        this.jsqlColumnName = "STUDENT_ID";
        
        this.jdbcColumnForDatabaseName = "name";
        this.jdbcColumnForTableName = "name";
        this.jdbcColumnForColumnName = "name";
        
        this.jdbcQueryForDatabaseNames = "select trim("+ this.jdbcColumnForDatabaseName +") "+ this.jdbcColumnForDatabaseName +" from sysibm.sysschemata";
        this.jdbcQueryForTableNames = "select "+ this.jdbcColumnForTableName +" from sysibm.systables where creator = '"+ this.jsqlDatabaseName +"'";
        this.jdbcQueryForColumnNames = "select "+ this.jdbcColumnForColumnName + " from sysibm.syscolumns where coltype != 'BLOB' and tbcreator = '"+ this.jsqlDatabaseName +"' and tbname = '"+ this.jsqlTableName +"'";
        this.jdbcQueryForValues = "SELECT "+ this.jsqlColumnName +" FROM "+ this.jsqlDatabaseName +"."+ this.jsqlTableName;
    }

    @AfterEach
    public void checkVendor() {
        Assertions.assertEquals(
            this.injectionModel.getMediatorVendor().getVendor(),
            this.injectionModel.getMediatorVendor().getDb2()
        );
    }
}
