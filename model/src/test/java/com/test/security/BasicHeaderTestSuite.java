package com.test.security;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;

import org.junitpioneer.jupiter.RepeatFailedTest;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.util.StringUtil;
import com.jsql.view.terminal.SystemOutTerminal;
import com.test.vendor.mysql.ConcreteMySqlErrorTestSuite;

import spring.security.SecurityConfiguration;

public class BasicHeaderTestSuite extends ConcreteMySqlErrorTestSuite {
    
    @Override
    public void setupInjection() throws Exception {
        
        InjectionModel model = new InjectionModel();
        this.injectionModel = model;

        model.addObserver(new SystemOutTerminal());

        model.getMediatorUtils().getParameterUtil().initializeQueryString("http://localhost:8080/basic");
        model.getMediatorUtils().getParameterUtil().setListQueryString(Arrays.asList(
            new SimpleEntry<>("tenant", "mysql-error"),
            new SimpleEntry<>("name", "")
        ));
        model
        .getMediatorUtils()
        .getParameterUtil()
        .setListHeader(Arrays.asList(
            new SimpleEntry<>(
                "Authorization",
                String.format(
                    "Basic %s",
                    StringUtil.base64Encode(
                        String.format(
                            "%s:%s",
                            SecurityConfiguration.BASIC_USERNAME,
                            SecurityConfiguration.BASIC_PASSWORD
                        )
                    )
                )
            )
        ));
        
        model.setIsScanning(true);
        
        model
        .getMediatorUtils()
        .getConnectionUtil()
        .withMethodInjection(model.getMediatorMethod().getQuery())
        .withTypeRequest("GET");
        
        model.beginInjection();
    }
    
    @Override
    @RepeatFailedTest(3)
    public void listDatabases() throws JSqlException {
        super.listDatabases();
    }
}
