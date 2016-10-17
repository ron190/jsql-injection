package com.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.test.vendor.cubrid.CubridNormalGetTestSuite;
import com.test.vendor.db2.DB2NormalGetTestSuite;
import com.test.vendor.informix.InformixNormalGetTestSuite;
import com.test.vendor.ingres.IngresNormalGetTestSuite;
import com.test.vendor.mysql.MysqlNormalHeaderTestSuite;
import com.test.vendor.oracle.OracleNormalGetTestSuite;
import com.test.vendor.postgre.PostgreNormalGetTestSuite;
import com.test.vendor.sqlserver.SQLServerNormalGetTestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    CubridNormalGetTestSuite.class,
    DB2NormalGetTestSuite.class,
    InformixNormalGetTestSuite.class,
    IngresNormalGetTestSuite.class,
    MysqlNormalHeaderTestSuite.class,
    OracleNormalGetTestSuite.class,
    PostgreNormalGetTestSuite.class,
    SQLServerNormalGetTestSuite.class,
})
public class AllNormalTestSuite {
    // Empty on purpose
}