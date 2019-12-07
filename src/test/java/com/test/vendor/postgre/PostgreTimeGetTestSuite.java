package com.test.vendor.postgre;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;

import org.junit.BeforeClass;

import com.jsql.model.InjectionModel;
import com.jsql.model.MediatorModel;
import com.jsql.model.injection.method.MethodInjection;
import com.jsql.model.injection.strategy.StrategyInjection;
import com.jsql.model.injection.vendor.Vendor;
import com.jsql.util.ConnectionUtil;
import com.jsql.util.ParameterUtil;
import com.jsql.view.terminal.SystemOutTerminal;

public class PostgreTimeGetTestSuite extends ConcretePostgreTestSuite {

    @BeforeClass
    public static void initialize() throws Exception {
        
        runSpringApplication();
        
        InjectionModel model = new InjectionModel();
        MediatorModel.register(model);
        model.displayVersion();

        MediatorModel.model().addObserver(new SystemOutTerminal());

        ParameterUtil.initQueryString("http://localhost:8080/greeting-time");
        ParameterUtil.initRequest("");
        ParameterUtil.setQueryString(Arrays.asList(
            new SimpleEntry<String, String>("tenant", "postgres"), 
            new SimpleEntry<String, String>("name", "1'")
        ));
        ConnectionUtil.setMethodInjection(MethodInjection.QUERY);
        ConnectionUtil.setTypeRequest("GET");
        
        MediatorModel.model().setIsScanning(true);
        MediatorModel.model().setStrategy(StrategyInjection.TIME);
        MediatorModel.model().setVendorByUser(Vendor.POSTGRESQL);
        MediatorModel.model().beginInjection();
    }

}
