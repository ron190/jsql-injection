package com.test.vendor.sqlite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.jsql.model.accessible.DataAccess;
import com.test.AbstractTestSuite;

public abstract class ConcreteSqliteTestSuite extends AbstractTestSuite {

    public ConcreteSqliteTestSuite() {
        this.config();
    }
    
    public void config() {
        
        // TODO Use same hibernate properties
        this.jdbcURL = "jdbc:sqlite:jsql-sqlite-its.db";
        this.jdbcUser = StringUtils.EMPTY;
        this.jdbcPass = StringUtils.EMPTY;
        this.jsqlDatabaseName = "musicstore";
        this.jsqlTableName = "Student";
        this.jsqlColumnName = "Student_Id";
        
        this.jdbcColumnForDatabaseName = "sqlite_master";
        this.jdbcColumnForTableName = "name";
        this.jdbcColumnForColumnName = "sql";
        
        this.jdbcQueryForDatabaseNames = "select '"+ this.jdbcColumnForDatabaseName +"' "+ this.jdbcColumnForDatabaseName +" from "+ this.jdbcColumnForDatabaseName +" WHERE type = 'table'";
        this.jdbcQueryForTableNames =    "select "+ this.jdbcColumnForTableName +" from sqlite_master WHERE type = 'table'";
        this.jdbcQueryForColumnNames =   "select "+ this.jdbcColumnForColumnName +" from "+ this.jdbcColumnForDatabaseName +" where tbl_name = '"+ this.jsqlTableName +"' and type = 'table'";
        this.jdbcQueryForValues =    "select "+ this.jsqlColumnName +" from "+ this.jsqlTableName;
    }
    
    @Override
    protected Collection<String> parse(List<String> rawColumns) {
        
        String modelColumns = rawColumns.stream().findFirst().orElseThrow(IllegalArgumentException::new);
        String columnsToParse = this.injectionModel.getMediatorVendor().getSqlite().transformSqlite(modelColumns);
        
        Matcher regexSearch =
            Pattern
            .compile(
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
}
