package com.test.vendor.sqlserver;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;

import org.junitpioneer.jupiter.RepeatFailedTest;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.view.terminal.SystemOutTerminal;

public class SqlServerBlindGetTestSuite extends ConcreteSqlServerTestSuite {

    @Override
    public void setupInjection() throws Exception {
        
        InjectionModel model = new InjectionModel();
        this.injectionModel = model;

        model.addObserver(new SystemOutTerminal());

        model.getMediatorUtils().getParameterUtil().initializeQueryString("http://localhost:8080/greeting-blind");
        model.getMediatorUtils().getParameterUtil().setListQueryString(Arrays.asList(
            new SimpleEntry<>("tenant", "sqlserver"),
            new SimpleEntry<>("name", "1'")
        ));
        
        model.getMediatorUtils().getPreferencesUtil().setIsNotInjectingMetadata(true);
        
        model
        .getMediatorUtils()
        .getConnectionUtil()
        .withMethodInjection(model.getMediatorMethod().getQuery())
        .withTypeRequest("GET");
        
        model.setIsScanning(true);
        model.getMediatorStrategy().setStrategy(model.getMediatorStrategy().getBlind());
        model.getMediatorVendor().setVendorByUser(model.getMediatorVendor().getSqlServer());
        model.beginInjection();
    }

    @Override
    @RepeatFailedTest(3)
    public void listValues() throws JSqlException {
        super.listValues();
    }
}
