package com.test.method;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.view.terminal.SystemOutTerminal;
import com.test.vendor.mysql.ConcreteMySqlSuiteIT;
import org.junitpioneer.jupiter.RetryingTest;

class HeaderSuiteIT extends ConcreteMySqlSuiteIT {

    @Override
    public void setupInjection() throws Exception {
        InjectionModel model = new InjectionModel();
        this.injectionModel = model;

        model.subscribe(new SystemOutTerminal());

        model.getMediatorUtils().getParameterUtil().initQueryString("http://localhost:8080/header");
        model.getMediatorUtils().getParameterUtil().initHeader("tenant: mysql\\r\\nname:");
        
        model.setIsScanning(true);

        model
        .getMediatorUtils()
        .getPreferencesUtil()
        .withIsStrategyBlindDisabled(true)
        .withIsStrategyTimeDisabled(true);
        
        model
        .getMediatorUtils()
        .getConnectionUtil()
        .setMethodInjection(model.getMediatorMethod().getHeader());
        
        model.beginInjection();
    }
    
    @Override
    @RetryingTest(3)
    public void listDatabases() throws JSqlException {
        super.listDatabases();
    }
}
