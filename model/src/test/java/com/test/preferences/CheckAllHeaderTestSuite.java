package com.test.preferences;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;

import org.junitpioneer.jupiter.RepeatFailedTest;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.view.terminal.SystemOutTerminal;
import com.test.vendor.mysql.ConcreteMySqlTestSuite;

public class CheckAllHeaderTestSuite extends ConcreteMySqlTestSuite {

    @Override
    public void setupInjection() throws Exception {
        
        InjectionModel model = new InjectionModel();
        this.injectionModel = model;

        model.addObserver(new SystemOutTerminal());

        model.getMediatorUtils().getParameterUtil().initializeQueryString("http://localhost:8080/greeting-header?tenant=mysql");
        model.getMediatorUtils().getParameterUtil().setListHeader(Arrays.asList(
            new SimpleEntry<>("fake1", "0'"),
            new SimpleEntry<>("name", ""),
            new SimpleEntry<>("fake2", "0'")
        ));
        
        model
        .getMediatorUtils()
        .getPreferencesUtil()
        .withCheckingAllHeaderParam()
        .withNotTestingConnection();
        
        model.getMediatorUtils().getConnectionUtil().setMethodInjection(model.getMediatorMethod().getHeader());
        
        model.setIsScanning(true);
        model.getMediatorStrategy().setStrategy(model.getMediatorStrategy().getNormal());
        model.beginInjection();
    }
    
    @Override
    @RepeatFailedTest(3)
    public void listDatabases() throws JSqlException {
        super.listDatabases();
    }
}
