package com.test.vendor.postgresql;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.view.terminal.SystemOutTerminal;
import org.junit.jupiter.api.Assertions;
import org.junitpioneer.jupiter.RetryingTest;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PostgreSqlReadFileSuiteIT extends ConcretePostgreSqlSuiteIT {
    
    @Override
    public void setupInjection() throws Exception {
        
        InjectionModel model = new InjectionModel();
        this.injectionModel = model;

        model.subscribe(new SystemOutTerminal());

        model.getMediatorUtils().getParameterUtil().initializeQueryString(
            "http://localhost:8080/normal?tenant=postgresql&name="
        );

        model
        .getMediatorUtils()
        .getConnectionUtil()
        .withMethodInjection(model.getMediatorMethod().getQuery())
        .withTypeRequest("GET");

        model
        .getMediatorUtils()
        .getPreferencesUtil()
        .withIsStrategyBlindDisabled(true)
        .withIsStrategyTimeDisabled(true)
        .withIsStrategyStackedDisabled(true)
        .withIsStrategyMultibitDisabled(true)
        .withIsStrategyErrorDisabled(true);

        model.getMediatorVendor().setVendorByUser(model.getMediatorVendor().getPostgreSQL());
        model.beginInjection();
    }
    
    @RetryingTest(3)
    public void readFile() throws JSqlException, ExecutionException, InterruptedException {

        List<String> contents = this.injectionModel.getResourceAccess()
                .readFile(Collections.singletonList("PG_VERSION"));

        LOGGER.info("ReadFile: found {} to find {}", String.join(",", contents).trim(), "9.6");

        Assertions.assertEquals("9.6", String.join(",", contents).trim());
    }
}
