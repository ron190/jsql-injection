package com.test.vendor.h2;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.jsql.model.InjectionModel;
import com.jsql.view.terminal.SystemOutTerminal;

@TestInstance(Lifecycle.PER_CLASS)
@Execution(ExecutionMode.CONCURRENT)
public class H2NormalGetTestSuite extends ConcreteH2TestSuite {
    
    @Override
    public void setupInjection() throws Exception {
        
        InjectionModel model = new InjectionModel();
        this.injectionModel = model;

        model.addObserver(new SystemOutTerminal());

        model.getMediatorUtils().getParameterUtil().initQueryString("http://localhost:8080/greeting");
        model.getMediatorUtils().getParameterUtil().initRequest("");
        model.getMediatorUtils().getParameterUtil().setQueryString(Arrays.asList(
            new SimpleEntry<>("tenant", "h2"),
            new SimpleEntry<>("name", "1'")
        ));
        model.getMediatorUtils().getConnectionUtil().setMethodInjection(model.mediatorMethodInjection.getQuery());
        model.getMediatorUtils().getConnectionUtil().setTypeRequest("GET");
        
        model.setStrategy(model.NORMAL);
        model.mediatorVendor.setVendorByUser(model.mediatorVendor.H2);
        model.beginInjection();
    }
    
}
