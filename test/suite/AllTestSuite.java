package suite;
import mysql.MysqlTestSuite;
import oracle.OracleNormalHeaderTestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    MysqlTestSuite.class,
    OracleNormalHeaderTestSuite.class
})
public class AllTestSuite {   
    
}