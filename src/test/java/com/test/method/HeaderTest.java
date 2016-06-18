package com.test.method;

import org.junit.BeforeClass;

import com.jsql.exception.PreparationException;
import com.jsql.model.injection.InjectionModel;
import com.jsql.model.injection.MediatorModel;
import com.jsql.model.strategy.Strategy;
import com.jsql.util.ConnectionUtil;
import com.jsql.view.junit.SystemOutTerminal;
import com.test.mysql.ConcreteMysqlTestSuite;

public class HeaderTest extends ConcreteMysqlTestSuite {

    @BeforeClass
    public static void initialize() throws PreparationException {
        InjectionModel model = new InjectionModel();
        MediatorModel.register(model);
        model.instanciationDone();
        new SystemOutTerminal();

        ConnectionUtil.initialUrl = "http://127.0.0.1/simulate_header.php";
        ConnectionUtil.headerData = "lib:0";
        ConnectionUtil.method = "HEADER";

        MediatorModel.model().inputValidation();

        MediatorModel.model().setStrategy(Strategy.NORMAL);
    }
}
