package com.test.vendor.exasol;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.view.terminal.SystemOutTerminal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junitpioneer.jupiter.RetryingTest;

class ExasolBlindBitGetSuiteIT extends ConcreteExasolSuiteIT {
    
    @Override
    public void setupInjection() throws Exception {
        InjectionModel model = new InjectionModel();
        this.injectionModel = model;

        model.subscribe(new SystemOutTerminal());

        model.getMediatorUtils().getParameterUtil().initQueryString(
            "http://localhost:8080/exasol?name=1'"  // remove when stable
        );

        model.setIsScanning(true);
        model  // remove when stable
        .getMediatorUtils()
        .getPreferencesUtil()
        .withIsNotSearchingCharInsertion(true)
        .withCountLimitingThreads(2)
        .withIsStrategyTimeDisabled(true)
        .withIsStrategyBlindBinDisabled(true)
        .withIsStrategyMultibitDisabled(true)
        .withIsStrategyErrorDisabled(true)
        .withIsStrategyStackDisabled(true)
        .withIsStrategyUnionDisabled(true);

        model
        .getMediatorUtils()
        .getConnectionUtil()
        .withMethodInjection(model.getMediatorMethod().getQuery())
        .withTypeRequest("GET");

        model.getMediatorVendor().setVendorByUser(model.getMediatorVendor().getExasol());  // remove when stable
        model.beginInjection();
    }

    @Override
    @RetryingTest(3)
    public void listValues() throws JSqlException {
        super.listValues();
    }

    @AfterEach
    void afterEach() {
        Assertions.assertEquals(
            this.injectionModel.getMediatorStrategy().getBlindBit(),
            this.injectionModel.getMediatorStrategy().getStrategy()
        );
    }
}
