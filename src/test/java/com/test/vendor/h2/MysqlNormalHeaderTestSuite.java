package com.test.vendor.h2;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.jsql.model.InjectionModel;
import com.jsql.model.MediatorModel;
import com.jsql.model.injection.method.MethodInjection;
import com.jsql.model.injection.strategy.StrategyInjection;
import com.jsql.model.injection.vendor.Vendor;
import com.jsql.util.ConnectionUtil;
import com.jsql.util.ParameterUtil;
import com.jsql.util.PreferencesUtil;
import com.jsql.view.terminal.SystemOutTerminal;

import spring.Application;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class MysqlNormalHeaderTestSuite extends ConcreteMysqlTestSuite {
    
    static ConfigurableApplicationContext ctx;
    
    @BeforeClass
    public static void initialize() throws Exception {
//        SpringApplication.run(Application.class, new String[] {});
//        Application.main2(new String[] {});
        Application.init();
//        Application.main(new String[] {});
        ctx = SpringApplication.run(Application.class, new String[] {});
        
        
        InjectionModel model = new InjectionModel();
        MediatorModel.register(model);
        model.displayVersion();

        MediatorModel.model().addObserver(new SystemOutTerminal());

        PreferencesUtil.setNotTestingConnection(true);
        
//        ParameterUtil.initQueryString("http://"+ AbstractTestSuite.HOSTNAME +"/simulate_header.php");
//        ParameterUtil.initRequest("");
//        ParameterUtil.initHeader("lib: 0");
//        ConnectionUtil.setMethodInjection(MethodInjection.HEADER);
//        ConnectionUtil.setTypeRequest("POST");
        
        ParameterUtil.initQueryString("http://localhost:8080/greeting");
        ParameterUtil.initRequest("");
//        ParameterUtil.initHeader("lib: 0");
        ParameterUtil.setQueryString(Arrays.asList(new SimpleEntry<String, String>("tenantId", "h2"), new SimpleEntry<String, String>("name", "1'")));
        ConnectionUtil.setMethodInjection(MethodInjection.QUERY);
        ConnectionUtil.setTypeRequest("GET");
        
        MediatorModel.model().setStrategy(StrategyInjection.NORMAL);
        MediatorModel.model().setVendorByUser(Vendor.H2);
        MediatorModel.model().beginInjection();
    }
    
    @AfterClass
    public static void stop() {
                ctx.close();
    }
    
}
