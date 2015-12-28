package com.test.sqlserver;

import org.junit.BeforeClass;

import com.jsql.exception.PreparationException;
import com.jsql.model.injection.InjectionModel;
import com.jsql.model.injection.MediatorModel;
import com.jsql.view.junit.SystemOutTerminal;

public class SQLServerBlindGetTestSuite extends ConcreteSQLServerTestSuite {

    @BeforeClass
    public static void initialize() throws PreparationException {
        InjectionModel model = new InjectionModel();
        MediatorModel.register(model);
        model.instanciationDone();
        new SystemOutTerminal();

        MediatorModel.model().initialUrl = "http://127.0.0.1/sqlserver_simulate_get.php";
        MediatorModel.model().getData = "?lib=1";
        MediatorModel.model().method = "GET";

        MediatorModel.model().inputValidation();

        MediatorModel.model().injectionStrategy = MediatorModel.model().blindStrategy;
    }

//    @Override
//    @Test
//    @Ignore
//    public void listTables() throws PreparationException, StoppableException {
//        // Empty on purpose
//    }
}
