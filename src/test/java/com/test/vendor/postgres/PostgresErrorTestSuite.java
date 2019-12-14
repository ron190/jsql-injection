package com.test.vendor.postgres;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.jsql.model.InjectionModel;
import com.jsql.model.MediatorModel;
import com.jsql.model.injection.method.MethodInjection;
import com.jsql.model.injection.strategy.StrategyInjection;
import com.jsql.model.injection.vendor.Vendor;
import com.jsql.util.ConnectionUtil;
import com.jsql.util.ParameterUtil;
import com.jsql.util.PreferencesUtil;
import com.jsql.view.terminal.SystemOutTerminal;

@TestInstance(Lifecycle.PER_CLASS)
public class PostgresErrorTestSuite extends ConcretePostgresTestSuite {

    public void initialize3() throws Exception {
        
        InjectionModel model = new InjectionModel();
        this.injectionModel = model;

        model.addObserver(new SystemOutTerminal());

        model.parameterUtil.initQueryString("http://localhost:8080/greeting-error");
        model.parameterUtil.initRequest("");
        model.parameterUtil.setQueryString(Arrays.asList(
            new SimpleEntry<String, String>("tenant", "postgres"), 
            new SimpleEntry<String, String>("name", "0'")
        ));

        model.connectionUtil.setMethodInjection(model.QUERY);
        model.connectionUtil.setTypeRequest("GET");
        
        model.setStrategy(model.ERROR);
        model.setVendorByUser(model.POSTGRESQL);
        model.beginInjection();
    }
    
}
