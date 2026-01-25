package com.test.special;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.view.subscriber.SubscriberLogger;
import com.test.engine.mysql.ConcreteMySqlSuiteIT;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junitpioneer.jupiter.RetryingTest;

class SoapSuiteIT extends ConcreteMySqlSuiteIT {
    
    @Override
    public void setupInjection() throws Exception {
        InjectionModel model = new InjectionModel();
        this.injectionModel = model;

        model.subscribe(new SubscriberLogger(model));

        model.getMediatorUtils().parameterUtil().initQueryString("http://localhost:8080/ws?tenant=mysql");
        model.getMediatorUtils().parameterUtil().initRequest(
            "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:gs=\"http://www.baeldung.com/springsoap/gen\">" +
            "    <soapenv:Header/>" +
            "    <soapenv:Body>" +
            "        <gs:getCountryRequest>" +
            "            <gs:name>1'</gs:name>" +
            "        </gs:getCountryRequest>" +
            "    </soapenv:Body>" +
            "</soapenv:Envelope>"
        );
        model.getMediatorUtils().preferencesUtil()
        .withIsNotTestingConnection(true)  // Expected error 500 on connection test (SQL failure)
        .withIsCheckingAllSoapParam(true)
        .withIsNotSearchingCharInsertion(true)
        .withIsStrategyTimeDisabled(true)
        .withIsStrategyBlindBinDisabled(true)
        .withIsStrategyBlindBitDisabled(true)
        .withIsStrategyMultibitDisabled(true);

        model.setIsScanning(true);
        
        model
        .getMediatorUtils()
        .connectionUtil()
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
    void afterEach() {
        Assertions.assertEquals(
            this.injectionModel.getMediatorStrategy().getUnion(),
            this.injectionModel.getMediatorStrategy().getStrategy()
        );
    }
}
