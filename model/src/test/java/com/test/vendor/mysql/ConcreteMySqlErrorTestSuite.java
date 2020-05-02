package com.test.vendor.mysql;

public abstract class ConcreteMySqlErrorTestSuite extends ConcreteMySqlTestSuite {

    @Override
    public void config() {
        
        super.config();
        
        this.jdbcURL = "jdbc:mysql://127.0.0.1:3307/musicstore";
    }
}
