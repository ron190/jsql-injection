package com.test.vendor.postgre;

import java.net.MalformedURLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.jsql.model.InjectionModel;
import com.jsql.model.MediatorModel;
import com.jsql.model.exception.InjectionFailureException;
import com.jsql.model.injection.method.MethodInjection;
import com.jsql.model.injection.strategy.StrategyInjection;
import com.jsql.model.injection.vendor.Vendor;
import com.jsql.util.ConnectionUtil;
import com.jsql.util.ParameterUtil;
import com.jsql.util.PreferencesUtil;
import com.jsql.view.terminal.SystemOutTerminal;
import com.test.AbstractTestSuite;

import spring.Application;

//@Ignore
public class PostgreNormalGetTestSuite extends ConcretePostgreTestSuite {
    
    static ConfigurableApplicationContext ctx;
    
    @BeforeClass
    public static void initialize() throws InjectionFailureException, MalformedURLException {
//        Application.init();
//        ctx = SpringApplication.run(Application.class, new String[] {});
//        
//        InjectionModel model = new InjectionModel();
//        MediatorModel.register(model);
//        model.displayVersion();
//
//        MediatorModel.model().addObserver(new SystemOutTerminal());
//
//        PreferencesUtil.setNotTestingConnection(true);
//        
//        ParameterUtil.initQueryString("http://localhost:8080/greeting");
//        ParameterUtil.initRequest("");
//        ParameterUtil.setQueryString(Arrays.asList(new SimpleEntry<String, String>("tenantId", "mysql"), new SimpleEntry<String, String>("name", "1'")));
//        ConnectionUtil.setMethodInjection(MethodInjection.QUERY);
//        ConnectionUtil.setTypeRequest("GET");
//        
//        MediatorModel.model().beginInjection();
//
//        MediatorModel.model().setStrategy(StrategyInjection.NORMAL);

        Application.init();
        ctx = SpringApplication.run(Application.class, new String[] {});
        
        InjectionModel model = new InjectionModel();
        MediatorModel.register(model);
        model.displayVersion();

        MediatorModel.model().addObserver(new SystemOutTerminal());

//        ConnectionUtil.setUrlBase("http://"+ AbstractTestSuite.HOSTNAME +"/pg_simulate_get.php");
//        ParameterUtil.setQueryString(Arrays.asList(new SimpleEntry<String, String>("lib", "0")));
//        ConnectionUtil.setMethodInjection(MethodInjection.QUERY);
        ParameterUtil.initQueryString("http://localhost:8080/greeting");
        ParameterUtil.initRequest("");
        ParameterUtil.setQueryString(Arrays.asList(new SimpleEntry<String, String>("tenantId", "postgres"), new SimpleEntry<String, String>("name", "1'")));
        ConnectionUtil.setMethodInjection(MethodInjection.QUERY);
        ConnectionUtil.setTypeRequest("GET");

        MediatorModel.model().setStrategy(StrategyInjection.NORMAL);
        MediatorModel.model().setVendorByUser(Vendor.POSTGRESQL);
        MediatorModel.model().beginInjection();

    }
    
    @AfterClass
    public static void stop() {
                ctx.close();
    }
    
}
