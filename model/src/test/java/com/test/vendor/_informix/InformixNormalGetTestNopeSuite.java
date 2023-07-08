package com.test.vendor._informix;

import com.jsql.model.exception.InjectionFailureException;
import org.junit.BeforeClass;
import org.junit.Ignore;

import java.sql.SQLException;

@Ignore
public class InformixNormalGetTestNopeSuite extends ConcreteInformixTestNopeSuite {

    public InformixNormalGetTestNopeSuite() throws ClassNotFoundException, SQLException {
        super();
    }

    @Override
    @BeforeClass
    public void setupInjection() throws InjectionFailureException {
//        InjectionModel model = new InjectionModel();
//        MediatorModel.register(model);
//        model.displayVersion();
//
//        MediatorGui.model().addObserver(new SystemOutTerminal());
//
//        ConnectionUtil.setUrlBase("http://"+ AbstractTestSuite.HOSTNAME +"/informix_simulate_get.php");
//        ParameterUtil.setQueryString(Arrays.asList(new SimpleEntry<String, String>("lib", "0")));
//        ConnectionUtil.setMethodInjection(MethodInjection.QUERY);
//
//        MediatorGui.model().beginInjection();
//
//        MediatorGui.model().setStrategy(StrategyInjection.NORMAL);
    }
}
