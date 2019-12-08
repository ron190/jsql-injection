package com.test.vendor.mysql;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    MySQLNormalTestSuite.class,
    MySQLErrorTestSuite.class,
    MySQLBlindTestSuite.class,
    MySQLTimeTestSuite.class,
})
public class MySQLTestSuite {
    // Empty on purpose
}