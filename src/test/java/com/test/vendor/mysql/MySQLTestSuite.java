package com.test.vendor.mysql;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@Ignore
@RunWith(Suite.class)
@Suite.SuiteClasses({
    MySQLBlindTestSuite.class,
    MySQLNormalTestSuite.class,
    MySQLErrorTestSuite.class,
    MysqlTimeHeaderTestSuite.class,
})
public class MySQLTestSuite {
    // Empty on purpose
}