package com.test.vendor.mysql;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.jsql.model.InjectionModel;
import com.jsql.model.MediatorModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.model.injection.method.MethodInjection;
import com.jsql.model.injection.strategy.StrategyInjection;
import com.jsql.util.ConnectionUtil;
import com.jsql.util.ParameterUtil;
import com.jsql.util.PreferencesUtil;
import com.jsql.view.terminal.SystemOutTerminal;

public class MySQLBlindTestSuite extends ConcreteMySQLTestSuite {

    @BeforeClass
    public static void initialize() throws Exception {

        runSpringApplication();
        
        InjectionModel model = new InjectionModel();
        MediatorModel.register(model);
        model.displayVersion();

        MediatorModel.model().addObserver(new SystemOutTerminal());

        PreferencesUtil.setNotTestingConnection(true);
        
        ParameterUtil.initQueryString("http://localhost:8080/greeting-blind");
        ParameterUtil.initRequest("");
        ParameterUtil.setQueryString(Arrays.asList(
            new SimpleEntry<String, String>("tenant", "mysql"), 
            new SimpleEntry<String, String>("name", "1'")
        ));

        ConnectionUtil.setMethodInjection(MethodInjection.QUERY);
        ConnectionUtil.setTypeRequest("GET");
        
        MediatorModel.model().setIsScanning(true);
        MediatorModel.model().setStrategy(StrategyInjection.BLIND);
        MediatorModel.model().beginInjection();
    }
    
    @Ignore
    @Override
    @Test
    public void listDatabases() throws JSqlException {
        LOGGER.info("Ignore: too slow");
    }
    
    @Ignore
    @Override
    @Test
    public void listTables() throws JSqlException {
        LOGGER.info("Ignore: too slow");
    }
    
    @Ignore
    @Override
    @Test
    public void listColumns() throws JSqlException {
        LOGGER.info("Ignore: too slow");
    }
    
}
