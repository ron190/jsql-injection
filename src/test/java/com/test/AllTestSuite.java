package com.test;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.test.method.MethodTestSuite;
import com.test.vendor.db2.DB2TestSuite;
import com.test.vendor.mysql.MysqlTestSuite;
import com.test.vendor.oracle.OracleTestSuite;
import com.test.vendor.postgre.PostgreTestSuite;
import com.test.vendor.sqlserver.SQLServerTestSuite;

@Ignore
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