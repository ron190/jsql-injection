package com.test.engine.clickhouse;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.view.subscriber.SubscriberLogger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junitpioneer.jupiter.RetryingTest;

@SuppressWarnings("java:S2699")
class ClickhouseBlindBinSuiteIT extends ConcreteClickhouseSuiteIT {

    @Override
    public void setupInjection() throws Exception {
        InjectionModel model = new InjectionModel();
        this.injectionModel = model;

        model.subscribe(new SubscriberLogger(model));

        model.getMediatorUtils().parameterUtil().initQueryString(
            "http://localhost:8080/clickhouse?name='"
        );

        model.setIsScanning(true);

        model
        .getMediatorUtils()
        .preferencesUtil()
        .withIsCheckingAllURLParam(false)
        .withCountLimitingThreads(2)  // for perf only
        .withIsNotSearchingCharInsertion(true)  // for perf only
        .withIsStrategyBlindBitDisabled(true)  // Time disabled no required: sleep 5s > 3s max on clickhouse
        .withIsStrategyUnionDisabled(true);

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
