package com.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.test.db2.DB2TestSuite;
import com.test.method.MethodTestSuite;
import com.test.mysql.MysqlTestSuite;
import com.test.oracle.OracleTestSuite;
import com.test.postgre.PostgreTestSuite;
import com.test.sqlserver.SQLServerTestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    MethodTestSuite.class,
    MysqlTestSuite.class,
    PostgreTestSuite.class,
    OracleTestSuite.class,
    SQLServerTestSuite.class,
    DB2TestSuite.class,
})
public class AllTestSuite {
    // Empty on purpose
}