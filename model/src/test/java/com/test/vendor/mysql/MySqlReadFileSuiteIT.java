package com.test.vendor.mysql;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.view.terminal.SystemOutTerminal;
import org.junit.jupiter.api.Assertions;
import org.junitpioneer.jupiter.RetryingTest;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MySqlReadFileSuiteIT extends ConcreteMySqlSuiteIT {
    
    @Override
    public void setupInjection() throws Exception {
        
        InjectionModel model = new InjectionModel();
        this.injectionModel = model;

        model.subscribe(new SystemOutTerminal());

        model.getMediatorUtils().getParameterUtil().initializeQueryString("http://localhost:8080/normal?tenant=mysql&name=");

        model
        .getMediatorUtils()
        .getConnectionUtil()
        .withMethodInjection(model.getMediatorMethod().getQuery())
        .withTypeRequest("GET");
        
        model.beginInjection();
    }
    
    @RetryingTest(3)
    public void readFile() throws JSqlException, ExecutionException, InterruptedException {

        List<String> contents = this.injectionModel.getResourceAccess()
                .readFile(Collections.singletonList("/var/lib/mysql-files/file-injection.txt"));

        LOGGER.info("ReadFile: found {} to find {}", String.join(",", contents), "inside");

        Assertions.assertEquals("inside", String.join(",", contents).trim());
    }
}
