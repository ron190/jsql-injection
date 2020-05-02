package com.test.soap;

import org.junitpioneer.jupiter.RepeatFailedTest;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.view.terminal.SystemOutTerminal;
import com.test.vendor.mysql.ConcreteMySqlErrorTestSuite;

public class SoapTestSuite extends ConcreteMySqlErrorTestSuite {
    
    @Override
    public void setupInjection() throws Exception {
        
        InjectionModel model = new InjectionModel();
        this.injectionModel = model;

        model.addObserver(new SystemOutTerminal());

        model.getMediatorUtils().getParameterUtil().initializeQueryString("http://localhost:8080/ws");
        model.getMediatorUtils().getParameterUtil().initializeRequest("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:gs=\"http://www.baeldung.com/springsoap/gen\"><soapenv:Header/><soapenv:Body><gs:getCountryRequest><gs:name>1'</gs:name></gs:getCountryRequest></soapenv:Body></soapenv:Envelope>");
        
        model.getMediatorUtils().getPreferencesUtil().setIsNotTestingConnection(true);
        model.getMediatorUtils().getPreferencesUtil().setIsCheckingAllSoapParam(true);

        model.getMediatorUtils().getConnectionUtil().setMethodInjection(model.getMediatorMethod().getRequest());
        model.getMediatorUtils().getConnectionUtil().setTypeRequest("POST");
        
        model.setIsScanning(true);
        model.beginInjection();
    }
    
    @Override
    @RepeatFailedTest(3)
    public void listDatabases() throws JSqlException {
        super.listDatabases();
    }
}
