package com.test.vendor.mysql;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    MySQLTimeTestSuite.class,
    MySQLBlindTestSuite.class,
    MySQLNormalTestSuite.class,
    MySQLErrorTestSuite.class,
})
public class MySQLTestSuite {
    // Empty on purpose
}