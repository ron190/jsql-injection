package com.test.engine.postgres;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.view.subscriber.SubscriberLogger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junitpioneer.jupiter.RetryingTest;

class PostgresTimeGetSuiteIT extends ConcretePostgresSuiteIT {

    @Override
    public void setupInjection() throws Exception {
        InjectionModel model = new InjectionModel();
        this.injectionModel = model;

        model.subscribe(new SubscriberLogger(model));

        model.getMediatorUtils().parameterUtil().initQueryString(
            "http://localhost:8080/time?tenant=postgres&name=1'"  // todo should be auto detected
        );
        
        model.setIsScanning(true);

        model
        .getMediatorUtils()
        .preferencesUtil()
        .withIsStrategyBlindBitDisabled(true)
        .withIsStrategyBlindBinDisabled(true);

        model
        .getMediatorUtils()
        .connectionUtil()
        .withMethodInjection(model.getMediatorMethod().getQuery())
        .withTypeRequest("GET");

        model.getMediatorEngine().setEngineByUser(model.getMediatorEngine().getPostgres());  // todo should be auto detected
        model.beginInjection();
    }
    
    @Override
    @RetryingTest(6)
    public void listValues() throws JSqlException {
        super.listValues();
    }

    @AfterEach
    void afterEach() {
        Assertions.assertEquals(
            this.injectionModel.getMediatorStrategy().getTime(),
            this.injectionModel.getMediatorStrategy().getStrategy()
        );
    }
}
