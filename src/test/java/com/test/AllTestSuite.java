package com.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.test.vendor.h2.H2TestSuite;
import com.test.vendor.mysql.MySQLTestSuite;
import com.test.vendor.postgres.PostgresTestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    MySQLTestSuite.class,
    PostgresTestSuite.class,
    H2TestSuite.class
})
public class AllTestSuite {
    // Empty on purpose
}