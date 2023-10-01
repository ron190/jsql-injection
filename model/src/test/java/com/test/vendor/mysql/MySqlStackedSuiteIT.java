package com.test.vendor.mysql;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.view.terminal.SystemOutTerminal;
import org.junit.jupiter.api.Assertions;
import org.junitpioneer.jupiter.RetryingTest;

public class MySqlStackedSuiteIT extends ConcreteMySqlSuiteIT {
    
    @Override
    public void setupInjection() throws Exception {
        
        InjectionModel model = new InjectionModel();
        this.injectionModel = model;

        model.subscribe(new SystemOutTerminal());

        model.getMediatorUtils().getParameterUtil().initializeQueryString("http://localhost:8080/stacked?tenant=mysql&name=");

        model
        .getMediatorUtils()
        .getConnectionUtil()
        .withMethodInjection(model.getMediatorMethod().getQuery())
        .withTypeRequest("GET");

        model.getMediatorStrategy().setStrategy(model.getMediatorStrategy().getStacked());
        model.beginInjection();
    }
    
    @Override
    @RetryingTest(3)
    public void listDatabases() throws JSqlException {
        super.listDatabases();
        Assertions.assertEquals(
            this.injectionModel.getMediatorStrategy().getStacked(),
            this.injectionModel.getMediatorStrategy().getStrategy()
        );
    }
    
    @Override
    @RetryingTest(3)
    public void listTables() throws JSqlException {
        super.listTables();
        Assertions.assertEquals(
            this.injectionModel.getMediatorStrategy().getStacked(),
            this.injectionModel.getMediatorStrategy().getStrategy()
        );
    }
    
    @Override
    @RetryingTest(3)
    public void listColumns() throws JSqlException {
        super.listColumns();
        Assertions.assertEquals(
            this.injectionModel.getMediatorStrategy().getStacked(),
            this.injectionModel.getMediatorStrategy().getStrategy()
        );
    }
    
    @Override
    @RetryingTest(3)
    public void listValues() throws JSqlException {
        super.listValues();
        Assertions.assertEquals(
            this.injectionModel.getMediatorStrategy().getStacked(),
            this.injectionModel.getMediatorStrategy().getStrategy()
        );
    }
}
