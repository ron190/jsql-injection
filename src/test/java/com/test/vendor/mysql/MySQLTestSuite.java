package com.test.vendor.mysql;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.runner.RunWith;

//@RunWith(Suite.class)
//@Suite.SuiteClasses({
//    MySQLNormalTestSuite.class,
//    MySQLErrorTestSuite.class,
//    MySQLBlindTestSuite.class,
//    MySQLTimeTestSuite.class,
//})
@RunWith(JUnitPlatform.class)
@SelectClasses({
    MySQLTimeTestSuite.class,
    MySQLBlindTestSuite.class,
    MySQLNormalTestSuite.class,
    MySQLErrorTestSuite.class,
})
public class MySQLTestSuite {
    // Empty on purpose
}