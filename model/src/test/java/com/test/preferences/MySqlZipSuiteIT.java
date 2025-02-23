package com.test.preferences;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.view.terminal.SystemOutTerminal;
import com.test.vendor.mysql.ConcreteMySqlSuiteIT;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junitpioneer.jupiter.RetryingTest;

class MySqlZipSuiteIT extends ConcreteMySqlSuiteIT {
    
    @Override
    public void setupInjection() throws Exception {
        InjectionModel model = new InjectionModel();
        this.injectionModel = model;

        model.subscribe(new SystemOutTerminal());

        model.getMediatorUtils().getParameterUtil().initQueryString(
            "http://localhost:8080/union?tenant=mysql&name="
        );
        
        model
        .getMediatorUtils()
        .getPreferencesUtil()
        .withIsZipStrategy(true)
        .withIsStrategyBlindDisabled(true)
        .withIsStrategyTimeDisabled(true);
        
        model
        .getMediatorUtils()
        .getConnectionUtil()
        .withMethodInjection(model.getMediatorMethod().getQuery())
        .withTypeRequest("GET");
        
        model.beginInjection();
    }
    
    @Override
    @RetryingTest(3)
    public void listDatabases() throws JSqlException {
        super.listDatabases();
    }
    
    @Override
    @RetryingTest(3)
    public void listTables() throws JSqlException {
        super.listTables();
    }
    
    @Override
    @RetryingTest(3)
    public void listColumns() throws JSqlException {
        super.listColumns();
    }
    
    @Override
    @RetryingTest(3)
    public void listValues() throws JSqlException {
        super.listValues();
    }

    @AfterEach
    void afterEach() {
        Assertions.assertEquals(
            this.injectionModel.getMediatorStrategy().getUnion(),
            this.injectionModel.getMediatorStrategy().getStrategy()
        );
    }
}
