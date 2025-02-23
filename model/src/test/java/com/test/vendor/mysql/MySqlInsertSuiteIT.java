package com.test.vendor.mysql;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.view.terminal.SystemOutTerminal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junitpioneer.jupiter.RetryingTest;

class MySqlInsertSuiteIT extends ConcreteMySqlErrorSuiteIT {

    @Override
    public void setupInjection() throws Exception {
        InjectionModel model = new InjectionModel();
        this.injectionModel = model;

        model.subscribe(new SystemOutTerminal());

        model.getMediatorUtils().getParameterUtil().initQueryString(
            "http://localhost:8080/insert?tenant=mysql-error&name="
        );

        model.setIsScanning(true);

        model
        .getMediatorUtils()
        .getPreferencesUtil()
        .withIsStrategyBlindDisabled(true)
        .withIsStrategyTimeDisabled(true);

        model
        .getMediatorUtils()
        .getConnectionUtil()
        .withMethodInjection(model.getMediatorMethod().getQuery())
        .withTypeRequest("GET");
        
        model.beginInjection();
    }
    
    // Insert API add row to the table: listValues() not usable
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
}
