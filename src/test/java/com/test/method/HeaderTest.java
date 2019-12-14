package com.test.method;

import java.util.AbstractMap;
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
import com.test.vendor.mysql.ConcreteMySQLTestSuite;

@Ignore
public abstract class HeaderTest extends ConcreteMySQLTestSuite {

    @BeforeClass
    public void initialize3() throws InjectionFailureException {
        InjectionModel model = new InjectionModel();
//        MediatorModel.register(model);
        model.displayVersion();

        model.addObserver(new SystemOutTerminal());

        model.connectionUtil.setUrlBase("http://"+ AbstractTestSuite.HOSTNAME +"/simulate_header.php");
        model.parameterUtil.setHeader(Arrays.asList(new AbstractMap.SimpleEntry<>("lib", "0")));
        model.connectionUtil.setMethodInjection(model.HEADER);

        model.beginInjection();

        model.setStrategy(model.NORMAL);
    }
    
}
