package com.test.vendor.mysql;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.view.terminal.SystemOutTerminal;
import org.junit.jupiter.api.Assertions;
import org.junitpioneer.jupiter.RetryingTest;

public class MySqlSelectSuiteIT extends ConcreteMySqlSuiteIT {

    @Override
    public void setupInjection() throws Exception {
        
        InjectionModel model = new InjectionModel();
        this.injectionModel = model;

        model.subscribe(new SystemOutTerminal());

        model.getMediatorUtils().getParameterUtil().initializeQueryString("http://localhost:8080/select?tenant=mysql&name=");

        model.setIsScanning(true);

        model.getMediatorVendor().getMySQL().instance().getModelYaml().getStrategy().getConfiguration().setEndingComment("");

        model
        .getMediatorUtils()
        .getPreferencesUtil()
        .withIsUrlRandomSuffixDisabled(false);

        model
        .getMediatorUtils()
        .getConnectionUtil()
        .withMethodInjection(model.getMediatorMethod().getQuery())
        .withTypeRequest("GET");
        
        model.beginInjection();
    }
    
    @Override
    @RetryingTest(3)
    public void listValues() throws JSqlException {
        super.listValues();
        Assertions.assertEquals(
            this.injectionModel.getMediatorStrategy().getTime(),
            this.injectionModel.getMediatorStrategy().getStrategy()
        );
    }
}
