package com.test.engine.hsqldb;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.view.subscriber.SubscriberLogger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junitpioneer.jupiter.RetryingTest;

class HsqldbBlindBinGetSuiteIT extends ConcreteHsqldbSuiteIT {
    
    @Override
    public void setupInjection() throws Exception {
        InjectionModel model = new InjectionModel();
        this.injectionModel = model;

        model.subscribe(new SubscriberLogger(model));

        model.getMediatorUtils().parameterUtil().initQueryString(
            "http://localhost:8080/blind?tenant=hsqldb&name="
        );

        model.setIsScanning(true);

        model
        .getMediatorUtils()
        .preferencesUtil()
        .withIsStrategyBlindBitDisabled(true);
        
        model
        .getMediatorUtils()
        .connectionUtil()
        .withMethodInjection(model.getMediatorMethod().getQuery())
        .withTypeRequest("GET");
        
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
            this.injectionModel.getMediatorStrategy().getBlindBin(),
            this.injectionModel.getMediatorStrategy().getStrategy()
        );
    }
}
