package com.test.engine.access;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.view.subscriber.SubscriberLogger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;

@SuppressWarnings("java:S2699")
class AccessUnionSuiteIT extends ConcreteAccessSuiteIT {

    @Override
    public void setupInjection() throws Exception {
        InjectionModel model = new InjectionModel();
        this.injectionModel = model;

        model.subscribe(new SubscriberLogger(model));

        model.getMediatorUtils().parameterUtil().initQueryString(
            "http://localhost:8080/access?name="
        );

        model  // remove when stable
        .getMediatorUtils()
        .preferencesUtil()
        .withIsStrategyBlindBinDisabled(true)
        .withIsStrategyBlindBitDisabled(true);

        model
        .getMediatorUtils()
        .connectionUtil()
        .withMethodInjection(model.getMediatorMethod().getQuery())
        .withTypeRequest("GET");

        model.getMediatorEngine().setEngineByUser(model.getMediatorEngine().getAccess());  // jdbc ucanaccess is hsqldb bridge

        model.beginInjection();
    }
    
    @Override
    @RepeatedTest(3)
    public void listDatabases() throws JSqlException {
        super.listDatabases();
    }
    
    @Override
    @RepeatedTest(3)
    public void listTables() throws JSqlException {
        super.listTables();
    }
    
    @Override
    @RepeatedTest(3)
    public void listColumns() throws JSqlException {
        super.listColumns();
    }
    
    @Override
    @RepeatedTest(3)
    public void listValues() throws JSqlException {
        super.listValues();
    }

    @AfterEach
    void afterEach() {
        Assertions.assertEquals(
            this.injectionModel.getMediatorStrategy().getUnion(),
            this.injectionModel.getMediatorStrategy().getStrategy()
        );
    }
}
