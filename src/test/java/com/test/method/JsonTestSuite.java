package com.test.method;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.jsql.model.InjectionModel;
import com.jsql.view.terminal.SystemOutTerminal;
import com.test.vendor.mysql.ConcreteMySQLTestSuite;

@TestInstance(Lifecycle.PER_CLASS)
@Execution(ExecutionMode.CONCURRENT)
public class JsonTestSuite extends ConcreteMySQLTestSuite {
    
    @Override
    public void setupInjection() throws Exception {
        
        InjectionModel model = new InjectionModel();
        this.injectionModel = model;

        model.addObserver(new SystemOutTerminal());

        model.getMediatorUtils().getParameterUtil().initQueryString("http://localhost:8080/greeting-json");
        model.getMediatorUtils().getParameterUtil().initRequest("");
        model.getMediatorUtils().getParameterUtil().setQueryString(Arrays.asList(
            new SimpleEntry<>("tenant", "mysql"),
            new SimpleEntry<>("name", "{\"b\":[null,null,{\"a\":\"0'\"}]}")
        ));
        
        model.getMediatorUtils().getPreferencesUtil().setCheckingAllURLParam(true);
        model.getMediatorUtils().getPreferencesUtil().setCheckingAllJSONParam(true);
        model.getMediatorUtils().getPreferencesUtil().setNotTestingConnection(true);
        model.getMediatorUtils().getConnectionUtil().setMethodInjection(model.getMediatorMethodInjection().getQuery());
        model.getMediatorUtils().getConnectionUtil().setTypeRequest("GET");
        
        model.setIsScanning(true);
        model.getMediatorStrategy().setStrategy(model.getMediatorStrategy().getNormal());
        model.beginInjection();
    
    }
    
}
