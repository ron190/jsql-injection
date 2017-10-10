package com.test.vendor.mysql;

import java.net.MalformedURLException;

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

public class MysqlBlindHeaderTestSuite extends ConcreteMysqlTestSuite {

    @BeforeClass
    public static void initialize() throws MalformedURLException {
        InjectionModel model = new InjectionModel();
        MediatorModel.register(model);
        model.displayVersion();

        MediatorModel.model().addObserver(new SystemOutTerminal());
        
        PreferencesUtil.setNotTestingConnection(true);
        
        ParameterUtil.initQueryString("http://"+ AbstractTestSuite.HOSTNAME +"/simulate_header.php");
        ParameterUtil.initRequest("");
        ParameterUtil.initHeader("lib: 1*");
        ConnectionUtil.setMethodInjection(MethodInjection.HEADER);
        ConnectionUtil.setTypeRequest("POST");
        
        MediatorModel.model().beginInjection();

        MediatorModel.model().setStrategy(StrategyInjection.BLIND);
    }
    
}
