package com.test.vendor.db2;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    DB2NormalGetTestSuite.class,
    DB2BlindGetTestSuite.class
})
public class DB2TestSuite {
    // Empty on purpose
}