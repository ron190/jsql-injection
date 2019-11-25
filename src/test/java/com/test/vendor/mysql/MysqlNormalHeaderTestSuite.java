package com.test.vendor.mysql;

import java.util.Arrays;
import java.util.AbstractMap.SimpleEntry;

import org.junit.BeforeClass;

import com.jsql.model.InjectionModel;
import com.jsql.model.MediatorModel;
import com.jsql.model.injection.method.MethodInjection;
import com.jsql.model.injection.strategy.StrategyInjection;
import com.jsql.util.ConnectionUtil;
import com.jsql.util.ParameterUtil;
import com.jsql.util.PreferencesUtil;
import com.jsql.view.terminal.SystemOutTerminal;
import com.test.AbstractTestSuite;

public class MysqlNormalHeaderTestSuite extends ConcreteMysqlTestSuite {
    
    @BeforeClass
    public static void initialize() throws Exception {
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
        ParameterUtil.setQueryString(Arrays.asList(new SimpleEntry<String, String>("tenantId", "tenantId3"), new SimpleEntry<String, String>("name", "1'")));
        ConnectionUtil.setMethodInjection(MethodInjection.QUERY);
        ConnectionUtil.setTypeRequest("GET");
        
        MediatorModel.model().beginInjection();

        MediatorModel.model().setStrategy(StrategyInjection.NORMAL);
    }
    
}
