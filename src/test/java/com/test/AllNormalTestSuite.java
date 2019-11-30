package com.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.test.vendor.h2.H2NormalGetTestSuite;
import com.test.vendor.mysql.MysqlErrobasedHeaderTestSuite;
import com.test.vendor.mysql.MysqlNormalHeaderTestSuite;
import com.test.vendor.postgre.PostgreNormalGetTestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    MysqlNormalHeaderTestSuite.class,
    MysqlErrobasedHeaderTestSuite.class,
    PostgreNormalGetTestSuite.class,
    H2NormalGetTestSuite.class
})
public class AllNormalTestSuite {
    // Empty on purpose
}