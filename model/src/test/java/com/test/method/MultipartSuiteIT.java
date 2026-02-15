package com.test.method;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.view.subscriber.SubscriberLogger;
import com.test.engine.mysql.ConcreteMysqlSuiteIT;
import org.junitpioneer.jupiter.RetryingTest;

class MultipartSuiteIT extends ConcreteMysqlSuiteIT {
    
    @Override
    public void setupInjection() throws Exception {
        InjectionModel model = new InjectionModel();
        this.injectionModel = model;

        model.subscribe(new SubscriberLogger(model));

        model.getMediatorUtils().parameterUtil().initQueryString("http://localhost:8080/multipart?tenant=mysql");
        model.getMediatorUtils().parameterUtil().initRequest(
            "--boundary\\nContent-Disposition: form-data; name=\"name\"\\n\\n'*\\n--boundary--"
        );
        model.getMediatorUtils().parameterUtil().initHeader("Content-Type: multipart/form-data;boundary=boundary");

        model.setIsScanning(true);

        model
        .getMediatorUtils()
        .preferencesUtil()
        .withIsCheckingAllURLParam(false)
        .withIsStrategyTimeDisabled(true)
        .withIsStrategyBlindBinDisabled(true)
        .withIsStrategyBlindBitDisabled(true);

        model
        .getMediatorUtils()
        .connectionUtil()
        .withMethodInjection(model.getMediatorMethod().getRequest())
        .withTypeRequest("POST");
        
        model.beginInjection();
    }
    
    @Override
    @RetryingTest(3)
    public void listDatabases() throws JSqlException {
        super.listDatabases();
    }
}
