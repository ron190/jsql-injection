package com.test.vendor.db2;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.view.subscriber.SubscriberLogger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;

@SuppressWarnings("java:S2699")
class Db2ErrorSuiteIT extends ConcreteDb2SuiteIT {
    //Unstable

    @Override
    public void setupInjection() throws Exception {
        InjectionModel model = new InjectionModel();
        this.injectionModel = model;

        model.subscribe(new SubscriberLogger(model));

        // Slow fingerprinting => star
        model.getMediatorUtils().getParameterUtil().initQueryString(
            "http://localhost:8080/errors?tenant=db2&name='*"
        );

        model
        .getMediatorUtils()
        .getPreferencesUtil()
        .withIsNotSearchingCharInsertion(true)
        .withIsStrategyBlindBitDisabled(true)
        .withIsStrategyBlindBinDisabled(true)
        .withIsStrategyStackDisabled(true);

        model
        .getMediatorUtils()
        .getConnectionUtil()
        .withMethodInjection(model.getMediatorMethod().getQuery())
        .withTypeRequest("GET");

        model.beginInjection();
    }

    // listDatabases unstable

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
            this.injectionModel.getMediatorStrategy().getError(),
            this.injectionModel.getMediatorStrategy().getStrategy()
        );
    }
}
