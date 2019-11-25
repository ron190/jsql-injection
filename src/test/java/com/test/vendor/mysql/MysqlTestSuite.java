package com.test.vendor.mysql;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@Ignore
@RunWith(Suite.class)
@Suite.SuiteClasses({
    MysqlBlindHeaderTestSuite.class,
    MysqlNormalHeaderTestSuite.class,
    MysqlErrobasedHeaderTestSuite.class,
    MysqlTimeHeaderTestSuite.class,
})
public class MysqlTestSuite {
    // Empty on purpose
}