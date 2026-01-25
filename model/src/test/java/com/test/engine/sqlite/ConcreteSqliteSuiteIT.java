package com.test.engine.sqlite;

import com.jsql.model.accessible.DataAccess;
import com.test.AbstractTestSuite;
import org.hibernate.cfg.JdbcSettings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import spring.SpringApp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ConcreteSqliteSuiteIT extends AbstractTestSuite {

    public ConcreteSqliteSuiteIT() {
        var property = SpringApp.get("sqlite");
        this.jdbcURL = property.getProperty(JdbcSettings.JAKARTA_JDBC_URL);
        this.jdbcUser = property.getProperty(JdbcSettings.JAKARTA_JDBC_USER);
        this.jdbcPass = property.getProperty(JdbcSettings.JAKARTA_JDBC_PASSWORD);

        this.databaseToInject = "musicstore";
        this.tableToInject = "Student";
        this.columnToInject = "Student_Id";
        
        this.queryAssertDatabases = "select 'sqlite_master' sqlite_master from sqlite_master WHERE type = 'table'";
        this.queryAssertTables = "select name from sqlite_master WHERE type = 'table'";
        this.queryAssertColumns = String.format("""
            select sql from sqlite_master
            where tbl_name = '%s'
            and type = 'table'
        """, this.tableToInject);
        this.queryAssertValues = String.format("select %s from %s", this.columnToInject, this.tableToInject);
    }
    
    @Override
    protected Collection<String> parse(List<String> rawColumns) {
        
        String modelColumns = rawColumns.stream().findFirst().orElseThrow();
        String columnsToParse = this.injectionModel.getMediatorEngine().getSqlite().transformSqlite(modelColumns);
        
        Matcher regexSearch = Pattern.compile(
                DataAccess.MODE
                + DataAccess.ENCLOSE_VALUE_RGX
                + DataAccess.CELL_TABLE
                + DataAccess.ENCLOSE_VALUE_RGX
            )
            .matcher(columnsToParse);

        List<String> columns = new ArrayList<>();
        
        while (regexSearch.find()) {
            columns.add(regexSearch.group(1));
        }
        
        return columns;
    }

    @AfterEach
    public void checkEngine() {
        Assertions.assertEquals(
            this.injectionModel.getMediatorEngine().getSqlite(),
            this.injectionModel.getMediatorEngine().getEngine()
        );
    }
}
