package com.test.preferences;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.view.terminal.SystemOutTerminal;
import com.test.vendor.mysql.ConcreteMySqlSuiteIT;
import org.junitpioneer.jupiter.RetryingTest;

class TamperingSuiteIT extends ConcreteMySqlSuiteIT {
    
    @Override
    public void setupInjection() throws Exception {
        InjectionModel model = new InjectionModel();
        this.injectionModel = model;

        model.subscribe(new SystemOutTerminal());

        model.getMediatorUtils().getParameterUtil().initQueryString("http://localhost:8080/post?tenant=mysql");
        model.getMediatorUtils().getParameterUtil().initRequest("name=");

        model
        .getMediatorUtils()
        .getPreferencesUtil()
        .withIsStrategyDnsDisabled(true)
        .withIsStrategyBlindBitDisabled(true)
        .withIsStrategyBlindBinDisabled(true)
        .withIsStrategyTimeDisabled(true);

        model
        .getMediatorUtils()
        .getTamperingUtil()
        .withVersionComment()
        .withEqualToLike()
        .withRandomCase()
        .withHexToChar()
        .withSpaceToMultilineComment();
        
        model.setIsScanning(true);
        
        model
        .getMediatorUtils()
        .getConnectionUtil()
        .withMethodInjection(model.getMediatorMethod().getRequest())
        .withTypeRequest("POST");
        
        model.beginInjection();
    }
    
    @Override
    @RetryingTest(3)
    public void listDatabases() throws JSqlException {
        super.listDatabases();
    }
}
