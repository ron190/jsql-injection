package com.test.vendor.sqlserver;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    SQLServerNormalGetTestSuite.class,
    SQLServerBlindGetTestSuite.class,
    SQLServerTimeGetTestSuite.class,
})
public class SQLServerTestSuite {
    // Empty on purpose
}