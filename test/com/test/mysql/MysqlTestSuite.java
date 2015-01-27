package com.test.mysql;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

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