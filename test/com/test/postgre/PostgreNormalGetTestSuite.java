package com.test.postgre;

import org.junit.BeforeClass;

import com.jsql.exception.PreparationException;
import com.jsql.model.injection.InjectionModel;
import com.jsql.model.injection.MediatorModel;
import com.jsql.model.strategy.NormalStrategy;
import com.jsql.view.junit.SystemOutTerminal;

public class PostgreNormalGetTestSuite extends ConcretePostgreTestSuite {
    
    @BeforeClass
    public static void initialize() throws PreparationException {
        InjectionModel model = new InjectionModel();
        MediatorModel.register(model);
        model.instanciationDone();
        new SystemOutTerminal();

        MediatorModel.model().initialUrl = "http://127.0.0.1/pg_simulate_get.php";
        MediatorModel.model().getData = "?lib=0";
        MediatorModel.model().method = "GET";

        MediatorModel.model().inputValidation();

        MediatorModel.model().injectionStrategy = new NormalStrategy();
    }
}
