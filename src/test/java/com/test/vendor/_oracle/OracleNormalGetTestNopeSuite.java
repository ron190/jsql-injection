package com.test.vendor._oracle;

import java.sql.SQLException;

import org.junit.Ignore;

import com.jsql.model.exception.InjectionFailureException;

@Ignore
public class OracleNormalGetTestNopeSuite extends ConcreteOracleTestNopeSuite {

    public OracleNormalGetTestNopeSuite() throws SQLException {
        super();
    }

    @Override
//    @BeforeClass
    public void setupInjection() throws InjectionFailureException {
//        InjectionModel model = new InjectionModel();
//        MediatorModel.register(model);
//        model.displayVersion();
//
//        MediatorGui.model().addObserver(new SystemOutTerminal());
//
//        ConnectionUtil.setUrlBase("http://"+ AbstractTestSuite.HOSTNAME +"/oracle_simulate_get.php");
//        ParameterUtil.setQueryString(Arrays.asList(new SimpleEntry<String, String>("lib", "0")));
//        ConnectionUtil.setMethodInjection(MethodInjection.QUERY);
//
//        MediatorGui.model().beginInjection();
//
//        MediatorGui.model().setStrategy(StrategyInjection.NORMAL);
    }
}
