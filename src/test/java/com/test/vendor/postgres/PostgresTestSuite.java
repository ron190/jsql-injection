package com.test.vendor.postgres;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    PostgresTimeGetTestSuite.class,
    PostgresBlindGetTestSuite.class,
    PostgresNormalGetTestSuite.class,
    PostgresErrorTestSuite.class,
})
public class PostgresTestSuite {
    // Empty on purpose
}