package com.test.vendor.mysql;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

public abstract class ConcreteMySqlErrorTestSuite extends ConcreteMySqlTestSuite {

    @Override
    public void config() {
        
        super.config();
        
        this.jdbcURL = "jdbc:mysql://127.0.0.1:3307/musicstore";
    }
}
