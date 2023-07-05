package com.test.method;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.view.terminal.SystemOutTerminal;
import com.test.vendor.mysql.ConcreteMySqlTestSuite;
import org.junitpioneer.jupiter.RepeatFailedTest;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

public class MultipartTestSuite extends ConcreteMySqlTestSuite {
    
    @Override
    public void setupInjection() throws Exception {
        
        InjectionModel model = new InjectionModel();
        this.injectionModel = model;

        model.subscribe(new SystemOutTerminal());

        model.getMediatorUtils().getParameterUtil().initializeQueryString("http://localhost:8080/multipart?tenant=mysql");
        model.getMediatorUtils().getParameterUtil().initializeRequest("--boundary\\nContent-Disposition: form-data; name=\"name\"\\n\\n'*\\n--boundary--");
        model.getMediatorUtils().getParameterUtil().setListHeader(List.of(
            new SimpleEntry<>("Content-Type", "multipart/form-data;boundary=boundary")
        ));

        model.setIsScanning(true);
        
        model
        .getMediatorUtils()
        .getConnectionUtil()
        .withMethodInjection(model.getMediatorMethod().getRequest())
        .withTypeRequest("POST");
        
        model.beginInjection();
    }
    
    @Override
    @RepeatFailedTest(3)
    public void listDatabases() throws JSqlException {
        super.listDatabases();
    }
}
