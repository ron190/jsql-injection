package com.test.vendor.cubrid;

import com.test.AbstractTestSuite;

public abstract class ConcreteCubridTestSuite extends AbstractTestSuite {

    public ConcreteCubridTestSuite() {
        this.config();
    }

    public void config() {
        
        this.jdbcURL = "jdbc:cubrid:jsql-cubrid:33000:demodb:::";
        this.jdbcUser = "";
        this.jdbcPass = "";
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
}
