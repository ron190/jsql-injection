package com.test.vendor.db2;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.view.terminal.SystemOutTerminal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;

@SuppressWarnings("java:S2699")
public class Db2UnionSuiteIT extends ConcreteDb2SuiteIT {
    
    @Override
    public void setupInjection() throws Exception {
        
        InjectionModel model = new InjectionModel();
        this.injectionModel = model;

        model.subscribe(new SystemOutTerminal());

        // Slow fingerprinting => star
        model.getMediatorUtils().getParameterUtil().initializeQueryString(
            "http://localhost:8080/union?tenant=db2&name='"
        );

        model
        .getMediatorUtils()
        .getPreferencesUtil()
        .withIsNotSearchingCharInsertion(true)
        .withIsStrategyBlindDisabled(true)
        .withIsStrategyStackDisabled(true);

        model
        .getMediatorUtils()
        .getConnectionUtil()
        .withMethodInjection(model.getMediatorMethod().getQuery())
        .withTypeRequest("GET");
        
        model.getMediatorVendor().setVendorByUser(model.getMediatorVendor().getDb2());
        model.beginInjection();
    }
    
    @Override
    @RepeatedTest(3)
    public void listDatabases() throws JSqlException {
        super.listDatabases();
    }
    
    @Override
    @RepeatedTest(3)
    public void listTables() throws JSqlException {
        super.listTables();
    }
    
    @Override
    @RepeatedTest(3)
    public void listColumns() throws JSqlException {
        super.listColumns();
    }
    
    @Override
    @RepeatedTest(3)
    public void listValues() throws JSqlException {
        super.listValues();
    }

    @AfterEach
    public void afterEach() {
        Assertions.assertEquals(
            this.injectionModel.getMediatorStrategy().getUnion(),
            this.injectionModel.getMediatorStrategy().getStrategy()
        );
    }
}