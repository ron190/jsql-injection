package com.test.auth;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.jsql.model.InjectionModel;
import com.jsql.view.terminal.SystemOutTerminal;
import com.test.vendor.mysql.ConcreteMySQLErrorTestSuite;

@TestInstance(Lifecycle.PER_CLASS)
@Execution(ExecutionMode.CONCURRENT)
public class BasicPreferencesTestSuite extends ConcreteMySQLErrorTestSuite {
    
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
        
        model.getMediatorUtils().getAuthenticationUtil().setAuthentication(true);
        model.getMediatorUtils().getAuthenticationUtil().setUsernameAuthentication("ron190");
        model.getMediatorUtils().getAuthenticationUtil().setPasswordAuthentication("p@ssw0rd");
        model.getMediatorUtils().getAuthenticationUtil().setAuthentication();
        
        model.getMediatorUtils().getConnectionUtil().setMethodInjection(model.getMediatorMethodInjection().getQuery());
        model.getMediatorUtils().getConnectionUtil().setTypeRequest("GET");
        
        model.setIsScanning(true);
        model.beginInjection();
    }
}
