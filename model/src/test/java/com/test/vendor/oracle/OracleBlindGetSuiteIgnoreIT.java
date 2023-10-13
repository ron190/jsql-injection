package com.test.vendor.oracle;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.view.terminal.SystemOutTerminal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junitpioneer.jupiter.RetryingTest;

public class OracleBlindGetSuiteIgnoreIT extends ConcreteOracleSuiteIT {
    
    @Override
    public void setupInjection() throws Exception {
        
        InjectionModel model = new InjectionModel();
        this.injectionModel = model;

        model.subscribe(new SystemOutTerminal());

        model.getMediatorUtils().getParameterUtil().initializeQueryString(
            "http://localhost:8080/blind?tenant=oracle&name="
        );
        
        model.setIsScanning(true);

        model
        .getMediatorUtils()
        .getPreferencesUtil()
        .withNotInjectingMetadata();
        
        model
        .getMediatorUtils()
        .getConnectionUtil()
        .withMethodInjection(model.getMediatorMethod().getQuery())
        .withTypeRequest("GET");
        
        model.getMediatorVendor().setVendorByUser(model.getMediatorVendor().getOracle());
        model.beginInjection();
    }
    
    @Override
    @RetryingTest(3)
    public void listValues() throws JSqlException {
        super.listValues();
    }

    @AfterEach
    public void afterEach() {
        Assertions.assertEquals(
            this.injectionModel.getMediatorStrategy().getBlind(),
            this.injectionModel.getMediatorStrategy().getStrategy()
        );
    }
}