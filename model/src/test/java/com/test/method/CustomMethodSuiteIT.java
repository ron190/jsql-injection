package com.test.method;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.view.terminal.SystemOutTerminal;
import com.test.vendor.mysql.ConcreteMySqlErrorSuiteIT;
import org.junitpioneer.jupiter.RetryingTest;

public class CustomMethodSuiteIT extends ConcreteMySqlErrorSuiteIT {

    public static final String CUSTOM_METHOD = "CUSTOM-JSQL";
    
    @Override
    public void setupInjection() throws Exception {
        InjectionModel model = new InjectionModel();
        this.injectionModel = model;

        model.subscribe(new SystemOutTerminal());

        model.getMediatorUtils().getParameterUtil().initQueryString("http://localhost:8080/custom?name=");
        model.getMediatorUtils().getParameterUtil().initRequest("tenant=mysql-error");

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
        .withTypeRequest(CustomMethodSuiteIT.CUSTOM_METHOD);
        
        model.beginInjection();
    }
    
    @Override
    @RetryingTest(3)
    public void listDatabases() throws JSqlException {
        super.listDatabases();
    }
}