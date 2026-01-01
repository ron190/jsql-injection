package com.test.vendor.cubrid;

import com.test.AbstractTestSuite;
import org.hibernate.cfg.JdbcSettings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import spring.SpringApp;

public abstract class ConcreteCubridSuiteIT extends AbstractTestSuite {

    public ConcreteCubridSuiteIT() {
        this.config();
    }

    public void config() {

        this.jdbcURL = SpringApp.propsCubrid.getProperty(JdbcSettings.JAKARTA_JDBC_URL);
        this.jdbcUser = SpringApp.propsCubrid.getProperty(JdbcSettings.JAKARTA_JDBC_USER);
        this.jdbcPass = SpringApp.propsCubrid.getProperty(JdbcSettings.JAKARTA_JDBC_PASSWORD);

        this.jsqlDatabaseName = "PUBLIC";
        this.jsqlTableName = "student";
        this.jsqlColumnName = "Student_Id";
        
        this.jdbcColumnForDatabaseName = "owner_name";
        this.jdbcColumnForTableName = "class_name";
        this.jdbcColumnForColumnName = "attr_name";
        
        this.jdbcQueryForDatabaseNames = "select " + this.jdbcColumnForDatabaseName + " from db_class";
        this.jdbcQueryForTableNames =    "select " + this.jdbcColumnForTableName + " from db_class where " + this.jdbcColumnForDatabaseName + "='"+ this.jsqlDatabaseName +"'";
        this.jdbcQueryForColumnNames =   "select " + this.jdbcColumnForColumnName + " from db_attribute c inner join db_class t on t." + this.jdbcColumnForTableName + " = c." + this.jdbcColumnForTableName + " where t." + this.jdbcColumnForDatabaseName + "='"+ this.jsqlDatabaseName +"' and t." + this.jdbcColumnForTableName + "='"+ this.jsqlTableName +"'";
        this.jdbcQueryForValues =    "select "+ this.jsqlColumnName +" from "+ this.jsqlTableName;
    }

    @AfterEach
    public void checkVendor() {
        Assertions.assertEquals(
            this.injectionModel.getMediatorVendor().getVendor(),
            this.injectionModel.getMediatorVendor().getCubrid()
        );
    }
}
