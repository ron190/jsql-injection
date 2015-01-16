package com.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.test.method.MethodTestSuite;
import com.test.mysql.MysqlTestSuite;
import com.test.oracle.OracleNormalHeaderTestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    MethodTestSuite.class,
    MysqlTestSuite.class,
    OracleNormalHeaderTestSuite.class,
})
public class AllTestSuite {   
    // Empty on purpose
}