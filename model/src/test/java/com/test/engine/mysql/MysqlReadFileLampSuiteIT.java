package com.test.engine.mysql;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.view.subscriber.SubscriberLogger;
import org.junit.jupiter.api.Assertions;
import org.junitpioneer.jupiter.RetryingTest;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

class MysqlReadFileLampSuiteIT extends ConcreteMysqlSuiteIT {

    @Override
    public void setupInjection() throws Exception {
        InjectionModel model = new InjectionModel();
        this.injectionModel = model;

        model.subscribe(new SubscriberLogger(model));

        model.getMediatorUtils().parameterUtil().initQueryString(
            "http://jsql-lamp:8079/php/get-pdo.php?id="
        );

        model.setIsScanning(true);

        model
        .getMediatorUtils()
        .preferencesUtil()
        .withIsCheckingAllURLParam(false)
        .withIsStrategyTimeDisabled(true)
        .withIsStrategyBlindBinDisabled(true)
        .withIsStrategyBlindBitDisabled(true)
        .withIsStrategyStackDisabled(true)
        .withIsStrategyMultibitDisabled(true)
        .withIsStrategyErrorDisabled(true);

        model
        .getMediatorUtils()
        .connectionUtil()
        .withMethodInjection(model.getMediatorMethod().getQuery())
        .withTypeRequest("GET");

        model.beginInjection();
    }

    @RetryingTest(3)
    public void readFile() throws JSqlException, ExecutionException, InterruptedException {
        List<String> contents = this.injectionModel.getResourceAccess().readFile(
            Collections.singletonList("/var/www/html/php/get-pdo.php")
        );
        LOGGER.info("ReadFile: found {}, to find {}", String.join(",", contents).trim(), "<?php");
        Assertions.assertTrue(String.join(",", contents).trim().contains("<?php"));
    }
}
