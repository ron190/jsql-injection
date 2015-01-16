package com.test.mysql;

import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;

import com.jsql.exception.PreparationException;
import com.jsql.model.injection.InjectionModel;
import com.jsql.model.injection.MediatorModel;
import com.jsql.model.strategy.NormalStrategy;
import com.jsql.view.println.SystemOutTerminal;

public class MysqlNormalHeaderTestSuite extends ConcreteMysqlTestSuite {
    
    @BeforeClass
    public static void initialize() throws PreparationException {
        InjectionModel model = new InjectionModel();
        MediatorModel.register(model);
        model.instanciationDone();
        new SystemOutTerminal();
        
        MediatorModel.model().initialUrl = "http://127.0.0.1/simulate_header.php";
        MediatorModel.model().headerData = "lib:0";
        MediatorModel.model().method = "HEADER";

        MediatorModel.model().inputValidation();
        
        MediatorModel.model().injectionStrategy = new NormalStrategy();
    }
}
