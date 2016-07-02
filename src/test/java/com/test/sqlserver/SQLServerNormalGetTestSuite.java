package com.test.sqlserver;

import org.junit.BeforeClass;

import com.jsql.model.InjectionModel;
import com.jsql.model.MediatorModel;
import com.jsql.model.exception.PreparationException;
import com.jsql.model.strategy.Strategy;
import com.jsql.util.ConnectionUtil;
import com.jsql.view.terminal.SystemOutTerminal;

public class SQLServerNormalGetTestSuite extends ConcreteSQLServerTestSuite {

    @BeforeClass
    public static void initialize() throws PreparationException {
        InjectionModel model = new InjectionModel();
        MediatorModel.register(model);
        model.sendVersionToView();
        new SystemOutTerminal();

        ConnectionUtil.initialUrl = "http://127.0.0.1/sqlserver_simulate_get.php";
        ConnectionUtil.getData = "?lib=0";
        ConnectionUtil.method = "GET";

        MediatorModel.model().inputValidation();

        MediatorModel.model().setStrategy(Strategy.NORMAL);
    }
}
