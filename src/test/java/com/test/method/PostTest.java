package com.test.method;

import java.util.Arrays;
import java.util.AbstractMap.SimpleEntry;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.InjectionFailureException;
import com.jsql.view.terminal.SystemOutTerminal;
import com.test.vendor.mysql.ConcreteMySQLTestSuite;

@TestInstance(Lifecycle.PER_CLASS)
@Execution(ExecutionMode.CONCURRENT)
public class PostTest extends ConcreteMySQLTestSuite {
    
    @Override
    public void setupInjection() throws Exception {
//        InjectionModel model = new InjectionModel();
//        MediatorModel.register(model);
//        model.displayVersion();
//
//        MediatorModel.model().addObserver(new SystemOutTerminal());
//
//        ConnectionUtil.setUrlBase("http://"+ AbstractTestSuite.HOSTNAME +"/simulate_post.php");
//        ParameterUtil.setRequest(Arrays.asList(new SimpleEntry<String, String>("lib", "0")));
//        ConnectionUtil.setMethodInjection(MethodInjection.REQUEST);
//
//        MediatorModel.model().beginInjection();
//
//        MediatorModel.model().setStrategy(StrategyInjection.NORMAL);
        
        InjectionModel model = new InjectionModel();
        this.injectionModel = model;

        model.addObserver(new SystemOutTerminal());

        model.parameterUtil.initQueryString("http://localhost:8080/greeting-request");
        model.parameterUtil.initRequest("name=0'&tenantId=mysql");
        model.parameterUtil.setRequest(Arrays.asList(
            new SimpleEntry<>("tenant", "mysql"),
            new SimpleEntry<>("name", "0'")
        ));
        
        model.connectionUtil.setMethodInjection(model.REQUEST);
        model.connectionUtil.setTypeRequest("POST");
        
        model.setIsScanning(true);
        model.setStrategy(model.NORMAL);
        model.beginInjection();
    
    }
    
}
