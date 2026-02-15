package com.test.security;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.view.subscriber.SubscriberLogger;
import com.test.engine.mysql.ConcreteMysqlErrorSuiteIT;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junitpioneer.jupiter.RetryingTest;
import spring.security.CsrfWebSecurity;

class CsrfHeaderSuiteIT extends ConcreteMysqlErrorSuiteIT {
    
    @Override
    public void setupInjection() throws Exception {
        InjectionModel model = new InjectionModel();
        this.injectionModel = model;

        model.subscribe(new SubscriberLogger(model));

        model.getMediatorUtils().parameterUtil().initQueryString(
            "http://localhost:8080/csrf?tenant=mysql&name="
        );
        
        model.setIsScanning(true);

        model
        .getMediatorUtils()
        .preferencesUtil()
        .withIsCheckingAllURLParam(false)
        .withIsProcessingCsrf(true)
        .withIsStrategyTimeDisabled(true)
        .withIsStrategyBlindBinDisabled(true)
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
    public void listDatabases() throws JSqlException {
        super.listDatabases();
    }

    @AfterEach
    void afterEach() {
        Assertions.assertEquals(
            this.injectionModel.getMediatorStrategy().getUnion(),
            this.injectionModel.getMediatorStrategy().getStrategy()
        );
    }

    @AfterAll
    @Order(Order.DEFAULT)
    synchronized void assertResult() {
        Assertions.assertTrue(CsrfWebSecurity.FILTER.count > 0);
        LOGGER.info("CsrfWebSecurity.filter.count: {}", CsrfWebSecurity.FILTER.count);
    }
}
