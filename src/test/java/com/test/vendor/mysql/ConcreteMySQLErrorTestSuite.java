package com.test.vendor.mysql;

public class ConcreteMySQLErrorTestSuite extends ConcreteMySQLTestSuite {

    public ConcreteMySQLErrorTestSuite () {
        super();
    }
    
    @Override
    public void config() {
        super.config();
        this.jdbcURL = "jdbc:mysql://127.0.0.1:3307/musicstore";
    }
    
}
