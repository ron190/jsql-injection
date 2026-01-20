package com.test.security;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.view.subscriber.SubscriberLogger;
import com.test.vendor.mysql.ConcreteMySqlErrorSuiteIT;
import org.junit.jupiter.api.*;
import org.junitpioneer.jupiter.RetryingTest;
import spring.security.BasicSecurityConfig;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BasicSuiteIT extends ConcreteMySqlErrorSuiteIT {
    
    @Override
    public void setupInjection() throws Exception {
        InjectionModel model = new InjectionModel();
        this.injectionModel = model;

        model.subscribe(new SubscriberLogger(model));

        model.getMediatorUtils().getParameterUtil().initQueryString(
            "http://localhost:8080/basic?tenant=mysql-error&name="
        );

        model
        .getMediatorUtils()
        .getPreferencesUtil()
        .withIsStrategyTimeDisabled(true)
        .withIsStrategyBlindBinDisabled(true)
        .withIsStrategyBlindBitDisabled(true);

        model
        .getMediatorUtils()
        .getAuthenticationUtil()
        .withAuthenticationEnabled()
        .withUsernameAuthentication(BasicSecurityConfig.BASIC_USERNAME)
        .withPasswordAuthentication(BasicSecurityConfig.BASIC_PASSWORD)
        .setAuthentication();

        model.setIsScanning(true);
        
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

    @AfterEach
    void afterEach() {
        Assertions.assertEquals(
            this.injectionModel.getMediatorStrategy().getError(),
            this.injectionModel.getMediatorStrategy().getStrategy()
        );
    }

    @AfterAll
    @Order(Order.DEFAULT)
    void assertResult() {
        Assertions.assertTrue(BasicSecurityConfig.FILTER.count > 0);
        LOGGER.info("BasicSecurityConfig.filter.count: {}", BasicSecurityConfig.FILTER.count);
    }
}
