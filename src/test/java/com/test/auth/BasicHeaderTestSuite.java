package com.test.auth;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;

import org.junit.Ignore;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junitpioneer.jupiter.RepeatFailedTest;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.util.StringUtil;
import com.jsql.view.terminal.SystemOutTerminal;
import com.test.vendor.mysql.ConcreteMySQLErrorTestSuite;

import spring.security.SecurityConfiguration;

@TestInstance(Lifecycle.PER_CLASS)
@Execution(ExecutionMode.CONCURRENT)
public class BasicHeaderTestSuite extends ConcreteMySQLErrorTestSuite {
    
    @Override
    public void setupInjection() throws Exception {
        
        InjectionModel model = new InjectionModel();
        this.injectionModel = model;

        model.addObserver(new SystemOutTerminal());

        model.getMediatorUtils().getParameterUtil().initializeQueryString("http://localhost:8080/basic/greeting");
        model.getMediatorUtils().getParameterUtil().setListQueryString(Arrays.asList(
            new SimpleEntry<>("tenant", "mysql-error"),
            new SimpleEntry<>("name", "0'")
        ));
        model.getMediatorUtils().getParameterUtil().setListHeader(Arrays.asList(
            new SimpleEntry<>("Authorization", "Basic " + StringUtil.base64Encode(SecurityConfiguration.BASIC_USERNAME + ":" + SecurityConfiguration.BASIC_PASSWORD))
        ));
        
        model.getMediatorUtils().getConnectionUtil().setMethodInjection(model.getMediatorMethodInjection().getQuery());
        model.getMediatorUtils().getConnectionUtil().setTypeRequest("GET");
        
        model.setIsScanning(true);
        model.beginInjection();
    }
    
    @RepeatFailedTest(3)
    public void listDatabases() throws JSqlException {
        super.listDatabases();
    }
}
