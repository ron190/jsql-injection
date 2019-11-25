package com.test.method;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@Ignore
@RunWith(Suite.class)
@Suite.SuiteClasses({
    GetTest.class,
//    PostTest.class,
//    CookieTest.class,
//    HeaderTest.class
})
public class MethodTestSuite {
    // Empty on purpose
}