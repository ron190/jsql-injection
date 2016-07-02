package com.test.method;

import org.junit.BeforeClass;

import com.jsql.model.InjectionModel;
import com.jsql.model.MediatorModel;
import com.jsql.model.exception.PreparationException;
import com.jsql.model.strategy.Strategy;
import com.jsql.util.ConnectionUtil;
import com.jsql.view.terminal.SystemOutTerminal;
import com.test.mysql.ConcreteMysqlTestSuite;

public class HeaderTest extends ConcreteMysqlTestSuite {

    @BeforeClass
    public static void initialize() throws PreparationException {
        InjectionModel model = new InjectionModel();
        MediatorModel.register(model);
        model.sendVersionToView();
        new SystemOutTerminal();

        ConnectionUtil.initialUrl = "http://127.0.0.1/simulate_header.php";
        ConnectionUtil.headerData = "lib:0";
        ConnectionUtil.method = "HEADER";

        MediatorModel.model().inputValidation();

        MediatorModel.model().setStrategy(Strategy.NORMAL);
    }
}
