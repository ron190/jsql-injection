package com.test.vendor.oracle;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.test.AbstractTestSuite;

@TestInstance(Lifecycle.PER_CLASS)
@Execution(ExecutionMode.CONCURRENT)
public abstract class ConcreteOracleTestSuite extends AbstractTestSuite {

    public ConcreteOracleTestSuite() {
        this.config();
    }
    
    public void config() {
        
        // ORA-12519, TNS:no appropriate service handler found
        // select * from v$resource_limit where resource_name = 'processes';
        
        this.jdbcURL = "jdbc:oracle:thin:@127.0.0.1:21521:xe";
        this.jdbcUser = "system";
        this.jdbcPass = "Password1_One";
        this.jsqlDatabaseName = "SYSTEM";
        this.jsqlTableName = "STUDENT";
        this.jsqlColumnName = "STUDENT_ID";
        
        this.jdbcColumnForDatabaseName = "owner";
        this.jdbcColumnForTableName = "table_name";
        this.jdbcColumnForColumnName = "column_name";
        
        this.jdbcQueryForDatabaseNames = "SELECT owner FROM all_tables";
        this.jdbcQueryForTableNames = "SELECT table_name FROM all_tables where owner='SYSTEM'";
        this.jdbcQueryForColumnNames = "SELECT column_name FROM all_tab_columns where owner='SYSTEM' and table_name='STUDENT'";
        this.jdbcQueryForValues = "SELECT STUDENT_ID FROM SYSTEM.STUDENT";
    }
}
