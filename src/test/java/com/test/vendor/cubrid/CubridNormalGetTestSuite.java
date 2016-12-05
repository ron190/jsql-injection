package com.test.vendor.cubrid;

import org.junit.BeforeClass;

import com.jsql.model.InjectionModel;
import com.jsql.model.MediatorModel;
import com.jsql.model.exception.InjectionFailureException;
import com.jsql.model.injection.method.MethodInjection;
import com.jsql.model.injection.strategy.Strategy;
import com.jsql.util.ConnectionUtil;
import com.jsql.view.terminal.SystemOutTerminal;
import com.test.AbstractTestSuite;

public class CubridNormalGetTestSuite extends ConcreteCubridTestSuite {

    public CubridNormalGetTestSuite() throws ClassNotFoundException {
        super();
    }

    @BeforeClass
    public static void initialize() throws InjectionFailureException {
        InjectionModel model = new InjectionModel();
        MediatorModel.register(model);
        model.displayVersion();
        
        MediatorModel.model().addObserver(new SystemOutTerminal());

        ConnectionUtil.setUrlBase("http://"+ AbstractTestSuite.HOSTNAME +"/cubrid_simulate_get.php");
        ConnectionUtil.setDataQuery("?lib=0");
        ConnectionUtil.setMethodInjection(MethodInjection.QUERY);

        MediatorModel.model().beginInjection();

        MediatorModel.model().setStrategy(Strategy.NORMAL);
    }
}
