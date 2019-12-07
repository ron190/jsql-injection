package com.test.vendor.postgre;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    PostgreBlindGetTestSuite.class,
    PostgreNormalGetTestSuite.class,
    PostgreTimeGetTestSuite.class,
})
public class PostgreTestSuite {
    // Empty on purpose
}