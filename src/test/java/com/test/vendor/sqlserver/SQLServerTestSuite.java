package com.test.vendor.sqlserver;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@Ignore
@RunWith(Suite.class)
@Suite.SuiteClasses({
    SQLServerNormalGetTestSuite.class,
    SQLServerBlindGetTestSuite.class,
    SQLServerTimeGetTestSuite.class,
})
public class SQLServerTestSuite {
    // Empty on purpose
}