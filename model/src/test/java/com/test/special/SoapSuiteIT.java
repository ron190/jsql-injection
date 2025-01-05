package com.test.special;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.view.terminal.SystemOutTerminal;
import com.test.vendor.mysql.ConcreteMySqlErrorSuiteIT;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junitpioneer.jupiter.RetryingTest;

public class SoapSuiteIT extends ConcreteMySqlErrorSuiteIT {
    
    @Override
    public void setupInjection() throws Exception {
        
        InjectionModel model = new InjectionModel();
        this.injectionModel = model;

        model.subscribe(new SystemOutTerminal());

        model.getMediatorUtils().getParameterUtil().initializeQueryString("http://localhost:8080/ws");
        model.getMediatorUtils().getParameterUtil().initializeRequest(
            "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:gs=\"http://www.baeldung.com/springsoap/gen\">" +
            "    <soapenv:Header/>" +
            "    <soapenv:Body>" +
            "        <gs:getCountryRequest>" +
            "            <gs:name>1'</gs:name>" +
            "        </gs:getCountryRequest>" +
            "    </soapenv:Body>" +
            "</soapenv:Envelope>"
        );
        model.getMediatorUtils().getPreferencesUtil()
        .withIsNotTestingConnection(true)  // Expected error 500 on connection test (SQL failure)
        .withIsCheckingAllSoapParam(true)
        .withIsNotSearchingCharInsertion(true)
        .withIsStrategyBlindDisabled(true)
        .withIsStrategyTimeDisabled(true)
        .withIsStrategyMultibitDisabled(true);

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

    @AfterEach
    public void afterEach() {
        Assertions.assertEquals(
            this.injectionModel.getMediatorStrategy().getNormal(),
            this.injectionModel.getMediatorStrategy().getStrategy()
        );
    }
}
