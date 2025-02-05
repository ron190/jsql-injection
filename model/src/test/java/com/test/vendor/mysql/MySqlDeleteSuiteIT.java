package com.test.vendor.mysql;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.view.terminal.SystemOutTerminal;
import org.junit.jupiter.api.Assertions;
import org.junitpioneer.jupiter.RetryingTest;

public class MySqlDeleteSuiteIT extends ConcreteMySqlErrorSuiteIT {

    @Override
    public void initTable() {
        this.jsqlTableName = "StudentForDelete";
    }

    @Override
    public void setupInjection() throws Exception {
        InjectionModel model = new InjectionModel();
        this.injectionModel = model;

        model.subscribe(new SystemOutTerminal());

        model.getMediatorUtils().getParameterUtil().initQueryString(
            "http://localhost:8080/delete?tenant=mysql-error&name="
        );

        model.setIsScanning(true);
        
        model
        .getMediatorUtils()
        .getConnectionUtil()
        .withMethodInjection(model.getMediatorMethod().getQuery())
        .withTypeRequest("GET");
        
        model.beginInjection();
    }

    @Override
    @RetryingTest(3)
    public void listDatabases() throws JSqlException {
        super.listDatabases();
        Assertions.assertEquals(
            this.injectionModel.getMediatorStrategy().getError(),
            this.injectionModel.getMediatorStrategy().getStrategy()
        );
    }
}
