package com.test.vendor.mysql;

public class ConcreteMysqlErrorTestSuite extends ConcreteMysqlTestSuite {

    public ConcreteMysqlErrorTestSuite () {
        super();
    }
    
    @Override
    public void config() {
        super.config();
        this.jdbcURL = "jdbc:mysql://127.0.0.1:3307/musicstore";
    }
    
}
