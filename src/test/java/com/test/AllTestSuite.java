package com.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.test.vendor.h2.H2NormalGetTestSuite;
import com.test.vendor.mysql.MySQLBlindTestSuite;
import com.test.vendor.mysql.MySQLErrorTestSuite;
import com.test.vendor.mysql.MySQLNormalTestSuite;
import com.test.vendor.mysql.MySQLTimeTestSuite;
import com.test.vendor.postgre.PostgreBlindGetTestSuite;
import com.test.vendor.postgre.PostgreNormalGetTestSuite;
import com.test.vendor.postgre.PostgreTimeGetTestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    MySQLNormalTestSuite.class,
    MySQLErrorTestSuite.class,
    MySQLBlindTestSuite.class,
    MySQLTimeTestSuite.class,
    PostgreNormalGetTestSuite.class,
    PostgreBlindGetTestSuite.class,
    PostgreTimeGetTestSuite.class,
    H2NormalGetTestSuite.class
})
public class AllTestSuite {
    // Empty on purpose
}