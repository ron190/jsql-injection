package com.test.preferences;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;

import org.junitpioneer.jupiter.RepeatFailedTest;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.view.terminal.SystemOutTerminal;
import com.test.vendor.mysql.ConcreteMySqlTestSuite;

public class MySqlDiosTestSuite extends ConcreteMySqlTestSuite {
    
    @Override
    public void setupInjection() throws Exception {
        
        InjectionModel model = new InjectionModel();
        this.injectionModel = model;

        model.subscribe(new SystemOutTerminal());

        model.getMediatorUtils().getParameterUtil().initializeQueryString("http://localhost:8080/normal");
        model.getMediatorUtils().getParameterUtil().setListQueryString(Arrays.asList(
            new SimpleEntry<>("tenant", "mysql-error"),
            new SimpleEntry<>("name", "")
        ));
        
        model
        .getMediatorUtils()
        .getPreferencesUtil()
        .withDiosStrategy();
        
        model
        .getMediatorUtils()
        .getConnectionUtil()
        .withMethodInjection(model.getMediatorMethod().getQuery())
        .withTypeRequest("GET");
        
        model.beginInjection();
    }
    
    @Override
    @RepeatFailedTest(3)
    public void listDatabases() throws JSqlException {
        super.listDatabases();
    }
    
    @Override
    @RepeatFailedTest(3)
    public void listTables() throws JSqlException {
        super.listTables();
    }
    
    @Override
    @RepeatFailedTest(3)
    public void listColumns() throws JSqlException {
        super.listColumns();
    }
    
    @Override
    @RepeatFailedTest(3)
    public void listValues() throws JSqlException {
        super.listValues();
    }
}
