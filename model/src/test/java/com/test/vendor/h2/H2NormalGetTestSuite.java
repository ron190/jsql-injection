package com.test.vendor.h2;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;

import org.junitpioneer.jupiter.RepeatFailedTest;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.view.terminal.SystemOutTerminal;

public class H2NormalGetTestSuite extends ConcreteH2TestSuite {
    
    @Override
    public void setupInjection() throws Exception {
        
        InjectionModel model = new InjectionModel();
        this.injectionModel = model;

        model.addObserver(new SystemOutTerminal());

        model.getMediatorUtils().getParameterUtil().initializeQueryString("http://localhost:8080/greeting");
        model.getMediatorUtils().getParameterUtil().setListQueryString(Arrays.asList(
            new SimpleEntry<>("tenant", "h2"),
            new SimpleEntry<>("name", "1'")
        ));
        
        model
        .getMediatorUtils()
        .getConnectionUtil()
        .withMethodInjection(model.getMediatorMethod().getQuery())
        .withTypeRequest("GET");
        
        model.getMediatorStrategy().setStrategy(model.getMediatorStrategy().getNormal());
        model.getMediatorVendor().setVendorByUser(model.getMediatorVendor().getH2());
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
