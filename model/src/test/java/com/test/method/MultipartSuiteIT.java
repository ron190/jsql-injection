package com.test.method;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.view.terminal.SystemOutTerminal;
import com.test.vendor.mysql.ConcreteMySqlSuiteIT;
import org.junitpioneer.jupiter.RetryingTest;

public class MultipartSuiteIT extends ConcreteMySqlSuiteIT {
    
    @Override
    public void setupInjection() throws Exception {
        InjectionModel model = new InjectionModel();
        this.injectionModel = model;

        model.subscribe(new SystemOutTerminal());

        model.getMediatorUtils().getParameterUtil().initQueryString("http://localhost:8080/multipart?tenant=mysql");
        model.getMediatorUtils().getParameterUtil().initRequest(
            "--boundary\\nContent-Disposition: form-data; name=\"name\"\\n\\n'*\\n--boundary--"
        );
        model.getMediatorUtils().getParameterUtil().initHeader("Content-Type: multipart/form-data;boundary=boundary");

        model.setIsScanning(true);

        model
        .getMediatorUtils()
        .getPreferencesUtil()
        .withIsStrategyBlindDisabled(true)
        .withIsStrategyTimeDisabled(true);
        
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
