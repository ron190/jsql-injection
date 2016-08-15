package com.test.vendor.oracle;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    OracleNormalGetTestSuite.class,
    OracleBlindGetTestSuite.class
})
public class OracleTestSuite {
    // Empty on purpose
}