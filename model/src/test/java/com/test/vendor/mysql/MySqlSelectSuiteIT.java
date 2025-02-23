package com.test.vendor.mysql;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.view.terminal.SystemOutTerminal;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junitpioneer.jupiter.RetryingTest;

class MySqlSelectSuiteIT extends ConcreteMySqlSuiteIT {

    @Override
    public void setupInjection() throws Exception {
        InjectionModel model = new InjectionModel();
        this.injectionModel = model;

        model.subscribe(new SystemOutTerminal());

        model.getMediatorUtils().getParameterUtil().initQueryString(
            "http://localhost:8080/select?tenant=mysql&name="
        );

        model.setIsScanning(true);

        model.getMediatorVendor().getMysql().instance().getModelYaml().getStrategy().getConfiguration().setEndingComment(StringUtils.EMPTY);

        model
        .getMediatorUtils()
        .getPreferencesUtil()
        .withIsUrlRandomSuffixDisabled(true)
        .withIsNotInjectingMetadata(true);

        model
        .getMediatorUtils()
        .getConnectionUtil()
        .withMethodInjection(model.getMediatorMethod().getQuery())
        .withTypeRequest("GET");
        
        model.beginInjection();
    }
    
    @Override
    @RetryingTest(3)
    public void listValues() throws JSqlException {
        super.listValues();
    }

    @AfterEach
    void afterEach() {
        Assertions.assertEquals(
            this.injectionModel.getMediatorStrategy().getTime(),
            this.injectionModel.getMediatorStrategy().getStrategy()
        );
    }
}
