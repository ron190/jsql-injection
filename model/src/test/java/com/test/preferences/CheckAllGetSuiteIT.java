package com.test.preferences;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.view.terminal.SystemOutTerminal;
import com.test.vendor.mysql.ConcreteMySqlSuiteIT;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junitpioneer.jupiter.RetryingTest;

public class CheckAllGetSuiteIT extends ConcreteMySqlSuiteIT {
    
    @Override
    public void setupInjection() throws Exception {
        
        InjectionModel model = new InjectionModel();
        this.injectionModel = model;

        model.subscribe(new SystemOutTerminal());

        model.getMediatorUtils().getParameterUtil().initializeQueryString(
            "http://localhost:8080/normal?tenant=mysql&name=&fake=empty"
        );
        
        model.setIsScanning(true);
        
        model
        .getMediatorUtils()
        .getPreferencesUtil()
        .withCheckingAllURLParam();
        
        model
        .getMediatorUtils()
        .getConnectionUtil()
        .withMethodInjection(model.getMediatorMethod().getQuery())
        .withTypeRequest("GET");
        
        model.beginInjection();
    }
    
    @Override
    @RetryingTest(maxAttempts = 3, suspendForMs = 1000)
    public void listDatabases() throws JSqlException {
        super.listDatabases();
    }

    @AfterEach
    public void afterEach() {
        Assertions.assertEquals(
            this.injectionModel.getMediatorStrategy().getNormal(),
            this.injectionModel.getMediatorStrategy().getStrategy()
        );
    }
}
