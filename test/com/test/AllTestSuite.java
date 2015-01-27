package com.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.test.method.MethodTestSuite;
import com.test.mysql.MysqlTestSuite;
import com.test.oracle.OracleTestSuite;
import com.test.postgre.PostgreTestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    MethodTestSuite.class,
    MysqlTestSuite.class,
    PostgreTestSuite.class,
    OracleTestSuite.class,
})
public class AllTestSuite {
    // Empty on purpose
}