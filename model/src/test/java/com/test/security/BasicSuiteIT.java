package com.test.security;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.view.terminal.SystemOutTerminal;
import com.test.vendor.mysql.ConcreteMySqlErrorSuiteIT;
import org.junit.jupiter.api.*;
import org.junitpioneer.jupiter.RetryingTest;
import spring.security.BasicSecurityConfig;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BasicSuiteIT extends ConcreteMySqlErrorSuiteIT {
    
    @Override
    public void setupInjection() throws Exception {
        
        InjectionModel model = new InjectionModel();
        this.injectionModel = model;

        model.subscribe(new SystemOutTerminal());

        model.getMediatorUtils().getParameterUtil().initializeQueryString("http://localhost:8080/basic");
        model.getMediatorUtils().getParameterUtil().setListQueryString(Arrays.asList(
            new SimpleEntry<>("tenant", "mysql-error"),
            new SimpleEntry<>("name", "")
        ));

        model
        .getMediatorUtils()
        .getAuthenticationUtil()
        .withAuthentEnabled()
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

    @AfterAll
    @Order(Order.DEFAULT)
    public void assertResult() {
        Assertions.assertTrue(BasicSecurityConfig.FILTER.count > 0);
        LOGGER.info("BasicSecurityConfig.filter.count: {}", BasicSecurityConfig.FILTER.count);
    }
}
