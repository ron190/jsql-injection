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
    public void initialize3() throws Exception {
        
        InjectionModel model = new InjectionModel();
        this.injectionModel = model;

        model.addObserver(new SystemOutTerminal());

        model.parameterUtil.initQueryString("http://localhost:8080/greeting");
        model.parameterUtil.initRequest("");
        model.parameterUtil.setQueryString(Arrays.asList(
            new SimpleEntry<>("tenant", "h2"),
            new SimpleEntry<>("name", "1'")
        ));
        model.connectionUtil.setMethodInjection(model.QUERY);
        model.connectionUtil.setTypeRequest("GET");
        
        model.setStrategy(model.NORMAL);
        model.setVendorByUser(model.H2);
        model.beginInjection();
    }
    
}
