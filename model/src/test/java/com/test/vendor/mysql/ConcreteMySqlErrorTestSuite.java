package com.test.vendor.mysql;

public abstract class ConcreteMySqlErrorTestSuite extends ConcreteMySqlTestSuite {

    @Override
    public void config() {
        
        super.config();
        
        this.jdbcURL = "jdbc:mysql://jsql-mysql-5-5-40:3307/musicstore";
    }
}
