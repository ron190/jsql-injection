package com.test.oracle;

import org.junit.BeforeClass;

import com.jsql.model.InjectionModel;
import com.jsql.model.MediatorModel;
import com.jsql.model.exception.InjectionFailureException;
import com.jsql.model.injection.method.MethodInjection;
import com.jsql.model.injection.strategy.Strategy;
import com.jsql.util.ConnectionUtil;
import com.jsql.view.terminal.SystemOutTerminal;
import com.test.AbstractTestSuite;

public class OracleNormalGetTestSuite extends ConcreteOracleTestSuite {

    @BeforeClass
    public static void initialize() throws InjectionFailureException {
        InjectionModel model = new InjectionModel();
        MediatorModel.register(model);
        model.sendVersionToView();

        MediatorModel.model().addObserver(new SystemOutTerminal());

        ConnectionUtil.urlBase = "http://"+ AbstractTestSuite.hostName +"/oracle_simulate_get.php";
        ConnectionUtil.dataQuery = "?lib=0";
        ConnectionUtil.methodInjection = MethodInjection.QUERY;

        MediatorModel.model().injection();

        MediatorModel.model().setStrategy(Strategy.NORMAL);
    }
}
