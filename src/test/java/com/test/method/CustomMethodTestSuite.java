package com.test.method;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junitpioneer.jupiter.RepeatFailedTest;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.view.terminal.SystemOutTerminal;
import com.test.vendor.mysql.ConcreteMySqlTestSuite;

@TestInstance(Lifecycle.PER_CLASS)
@Execution(ExecutionMode.CONCURRENT)
public class CustomMethodTestSuite extends ConcreteMySqlTestSuite {
    
    @Override
    public void setupInjection() throws Exception {
        
        InjectionModel model = new InjectionModel();
        this.injectionModel = model;

        model.addObserver(new SystemOutTerminal());

        model.getMediatorUtils().getParameterUtil().initializeQueryString("http://localhost:8080/greeting-custom?tenant=mysql-error&name=0'");
        model.getMediatorUtils().getParameterUtil().setListRequest(Arrays.asList(
            new SimpleEntry<>("tenant", "mysql-error"),
            new SimpleEntry<>("name", "0'")
        ));
        
        model.getMediatorUtils().getPreferencesUtil().setIsNotTestingConnection(true);
        model.getMediatorUtils().getConnectionUtil().setMethodInjection(model.getMediatorMethod().getQuery());
        model.getMediatorUtils().getConnectionUtil().setTypeRequest("CUSTOM-JSQL");
        
        model.setIsScanning(true);
        model.getMediatorStrategy().setStrategy(model.getMediatorStrategy().getNormal());
        model.beginInjection();
    }
    
    @Override
    @RepeatFailedTest(3)
    public void listDatabases() throws JSqlException {
        super.listDatabases();
    }
}
