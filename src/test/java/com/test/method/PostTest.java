package com.test.method;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Ignore;

import com.jsql.model.InjectionModel;
import com.jsql.model.MediatorModel;
import com.jsql.model.exception.InjectionFailureException;
import com.jsql.model.injection.method.MethodInjection;
import com.jsql.model.injection.strategy.StrategyInjection;
import com.jsql.util.ConnectionUtil;
import com.jsql.util.ParameterUtil;
import com.jsql.view.terminal.SystemOutTerminal;
import com.test.AbstractTestSuite;
import com.test.vendor.mysql.ConcreteMysqlTestSuite;

@Ignore
public class PostTest extends ConcreteMysqlTestSuite {

    @BeforeClass
    public static void initialize() throws InjectionFailureException {
        InjectionModel model = new InjectionModel();
        MediatorModel.register(model);
        model.displayVersion();

        MediatorModel.model().addObserver(new SystemOutTerminal());

        ConnectionUtil.setUrlBase("http://"+ AbstractTestSuite.HOSTNAME +"/simulate_post.php");
        ParameterUtil.setRequest(Arrays.asList(new SimpleEntry<String, String>("lib", "0")));
        ConnectionUtil.setMethodInjection(MethodInjection.REQUEST);

        MediatorModel.model().beginInjection();

        MediatorModel.model().setStrategy(StrategyInjection.NORMAL);
    }
    
}
