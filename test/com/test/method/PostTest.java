package com.test.method;

import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;

import com.jsql.exception.PreparationException;
import com.jsql.model.injection.InjectionModel;
import com.jsql.model.injection.MediatorModel;
import com.jsql.model.strategy.NormalStrategy;
import com.jsql.view.println.SystemOutTerminal;
import com.test.mysql.ConcreteMysqlTestSuite;

public class PostTest extends ConcreteMysqlTestSuite {
    
    @BeforeClass
    public static void initialize() throws PreparationException {
        InjectionModel model = new InjectionModel();
        MediatorModel.register(model);
        model.instanciationDone();
        new SystemOutTerminal();
        
        MediatorModel.model().initialUrl = "http://127.0.0.1/simulate_post.php";
        MediatorModel.model().postData = "lib=0";
        MediatorModel.model().method = "POST";

        MediatorModel.model().inputValidation();
        
        MediatorModel.model().injectionStrategy = new NormalStrategy();
    }
}
