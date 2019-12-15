package com.test.method;

import org.junit.BeforeClass;
import org.junit.Ignore;

import com.jsql.model.exception.InjectionFailureException;
import com.test.vendor.mysql.ConcreteMySQLTestSuite;

@Ignore
public abstract class PostTest extends ConcreteMySQLTestSuite {

    @Override
    @BeforeClass
    public void initialize3() throws InjectionFailureException {
//        InjectionModel model = new InjectionModel();
//        MediatorModel.register(model);
//        model.displayVersion();
//
//        MediatorModel.model().addObserver(new SystemOutTerminal());
//
//        ConnectionUtil.setUrlBase("http://"+ AbstractTestSuite.HOSTNAME +"/simulate_post.php");
//        ParameterUtil.setRequest(Arrays.asList(new SimpleEntry<String, String>("lib", "0")));
//        ConnectionUtil.setMethodInjection(MethodInjection.REQUEST);
//
//        MediatorModel.model().beginInjection();
//
//        MediatorModel.model().setStrategy(StrategyInjection.NORMAL);
    }
    
}
