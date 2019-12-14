package com.test.vendor.mysql;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@TestInstance(Lifecycle.PER_CLASS)
@Execution(ExecutionMode.CONCURRENT)
public abstract class ConcreteMySQLErrorTestSuite extends ConcreteMySQLTestSuite {

    public ConcreteMySQLErrorTestSuite () {
        super();
    }
    
    @Override
    public void config() {
        super.config();
        this.jdbcURL = "jdbc:mysql://127.0.0.1:3307/musicstore";
    }
    
}
